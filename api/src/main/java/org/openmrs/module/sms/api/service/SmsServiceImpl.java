package org.openmrs.module.sms.api.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatuses;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.task.SmsScheduledTask;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.util.Constants;
import org.openmrs.module.sms.api.util.SmsEventParams;
import org.openmrs.module.sms.api.util.SmsEventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.joda.time.DateTime.now;
import static org.openmrs.module.sms.api.audit.SmsDirection.OUTBOUND;
import static org.openmrs.module.sms.api.util.SmsEvents.outboundEvent;

//todo: final pass over how we use motechId system-wide

/**
 * Send an SMS, we really don't send here, but rather pass it on to the SmsHttpService which does
 */
public class SmsServiceImpl extends BaseOpenmrsService implements SmsService {

    private static final Log LOGGER = LogFactory.getLog(SmsServiceImpl.class);

    private SmsEventService smsEventService;
    private SmsSchedulerService schedulerService;
    private TemplateService templateService;
    private ConfigService configService;
    private SmsRecordDao smsRecordDao;

    @Autowired
    public SmsServiceImpl(SmsEventService smsEventService, SmsSchedulerService schedulerService,
                          @Qualifier("templateService") TemplateService templateService,
                          @Qualifier("configService") ConfigService configService,
                          SmsRecordDao smsRecordDao) {
        this.smsEventService = smsEventService;
        this.schedulerService = schedulerService;
        this.templateService = templateService;
        this.configService = configService;
        this.smsRecordDao = smsRecordDao;
    }

    private static List<String> splitMessage(String message, int maxSize, String header, String footer,
                                             boolean excludeLastFooter) {
        List<String> parts = new ArrayList<String>();
        int messageLength = message.length();

        if (messageLength <= maxSize) {
            parts.add(message);
        } else {
            //NOTE: since the format placeholders $m and $t are two characters wide and we assume no more than
            //99 parts, we don't need to do a String.format() to figure out the length of the actual header/footer
            String headerTemplate = header + "\n";
            String footerTemplate = "\n" + footer;
            int textSize = maxSize - headerTemplate.length() - footerTemplate.length();
            Integer numberOfParts = (int) Math.ceil(messageLength / (double) textSize);
            String numberOfPartsString = numberOfParts.toString();

            for (Integer i = 1; i <= numberOfParts; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(headerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
                if (i.equals(numberOfParts)) {
                    sb.append(message.substring((i - 1) * textSize));
                    if (!excludeLastFooter) {
                        sb.append(footerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
                    }
                } else {
                    sb.append(message.substring((i - 1) * textSize, (i - 1) * textSize + textSize));
                    sb.append(footerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
                }
                parts.add(sb.toString());
            }
        }
        return parts;
    }

    private List<List<String>> splitRecipientList(List<String> list, Integer maxSize) {
        List<List<String>> ret = new ArrayList<>();
        int i = 0;
        ArrayList<String> chunk = new ArrayList<>();
        for (String val : list) {
            chunk.add(val);
            i++;
            if (i % maxSize == 0) {
                ret.add(chunk);
                chunk = new ArrayList<>();
            }
        }
        if (chunk.size() > 0) {
            ret.add(chunk);
        }
        return ret;
    }

    private String generateMotechId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Sends an SMS
     */
    @Override
    @Transactional
    public void send(OutgoingSms sms) {

        if (!configService.hasConfigs()) {
            String message = String.format("Trying to send an SMS, but there are no SMS configs on this server. " +
                    "outgoingSms = %s", sms.toString());
            throw new IllegalStateException(message);
        }

        Config config = configService.getConfigOrDefault(sms.getConfig());
        Template template = templateService.getTemplate(config.getTemplateName());

        //todo: die if things aren't right, right?
        //todo: SMS_SCHEDULE_FUTURE_SMS research if any sms provider provides that, for now assume not.

        Integer maxSize = template.getOutgoing().getMaxSmsSize();
        String header = config.getSplitHeader();
        String footer = config.getSplitFooter();
        Boolean excludeLastFooter = config.getExcludeLastFooter();
        //todo: maximum number of supported recipients : per template/provider and/or per http specs

        //todo - cr - move that to the Config object so calculated only once ?
        //todo - cr - investigate if that might be a problem on windows
        // -2 to account for the added \n after the header and before the footer
        if ((maxSize - header.length() - footer.length() - 2) <= 0) {
            throw new IllegalArgumentException(
                    "The combined sizes of the header and footer templates are larger than the maximum SMS size!");
        }

        List<String> messageParts = splitMessage(sms.getMessage(), maxSize, header, footer, excludeLastFooter);
        List<List<String>> recipientsList = splitRecipientList(sms.getRecipients(),
                template.getOutgoing().getMaxRecipient());

        //todo: delivery_time on the sms provider's side if they support it?
        for (List<String> recipients : recipientsList) {
            if (sms.hasDeliveryTime()) {
                DateTime dt = sms.getDeliveryTime();
                for (String part : messageParts) {
                    String motechId = generateMotechId();
                    SmsEvent event = outboundEvent(SmsEventSubjects.SCHEDULED, config.getName(), recipients, part,
                            motechId, null, null, null, null, sms.getCustomParams());
                    //MOTECH scheduler needs unique job ids, so adding motechId as job_id_key will do that
                    event.getParameters().put(Constants.PARAM_JOB_ID, motechId);
                    event.getParameters().put(SmsEventParams.DELIVERY_TIME, dt);
                    schedulerService.safeScheduleRunOnceJob(event, dt.toDate(), new SmsScheduledTask());
                    LOGGER.info(String.format("Scheduling message [%s] to [%s] at %s.",
                            part.replace("\n", "\\n"), recipients, sms.getDeliveryTime()));
                    //add one millisecond to the next sms part so they will be delivered in order
                    //without that it seems Quartz doesn't fire events in the order they were scheduled
                    dt = dt.plus(1);
                    for (String recipient : recipients) {
                        smsRecordDao.create(new SmsRecord(config.getName(), OUTBOUND, recipient, part, now(),
                                DeliveryStatuses.SCHEDULED, null, motechId, null, null));
                    }
                }
            } else {
                for (String part : messageParts) {
                    String motechId = generateMotechId();
                    smsEventService.sendEventMessage(outboundEvent(SmsEventSubjects.PENDING, config.getName(), recipients,
                            part, motechId, null, null, null, null, sms.getCustomParams()));
                    LOGGER.info(String.format("Sending message [%s] to [%s].",
                            part.replace("\n", "\\n"), recipients));
                    for (String recipient : recipients) {
                        smsRecordDao.create(new SmsRecord(config.getName(), OUTBOUND, recipient, part, now(),
                                DeliveryStatuses.PENDING, null, motechId, null, null));
                    }
                }
            }
        }
    }
}
