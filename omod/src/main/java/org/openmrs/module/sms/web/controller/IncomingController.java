package org.openmrs.module.sms.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.event.listener.EventRelay;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.SmsRecordsDataService;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatuses;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.service.ConfigService;
import org.openmrs.module.sms.api.service.TemplateService;
import org.openmrs.module.sms.api.templates.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.openmrs.module.sms.api.audit.SmsDirection.INBOUND;
import static org.openmrs.module.sms.api.util.SmsEvents.inboundEvent;

/**
 * Handles http requests to {motechserver}/motech-platform-server/module/sms/incoming{Config} sent by sms providers
 * when they receive an SMS
 */
@Controller
@RequestMapping(value = "/incoming")
public class IncomingController {

    private static final String SMS_MODULE = "motech-sms";

    private static final Log LOGGER = LogFactory.getLog(IncomingController.class);

    private TemplateService templateService;
    private ConfigService configService;
    private EventRelay eventRelay;
    private SmsRecordsDataService smsRecordsDataService;
    private StatusMessageService statusMessageService;


    @Autowired
    public IncomingController(SmsRecordsDataService smsRecordsDataService,
                              @Qualifier("templateService") TemplateService templateService,
                              @Qualifier("configService") ConfigService configService,
                              StatusMessageService statusMessageService, EventRelay eventRelay) {
        this.smsRecordsDataService = smsRecordsDataService;
        this.templateService = templateService;
        this.configService = configService;
        this.statusMessageService = statusMessageService;
        this.eventRelay = eventRelay;
    }


    //todo: add provider-specific UI to explain how implementers must setup their providers' incoming callback

    /**
     * Handles an incoming SMS notification coming from the provider. A MOTECH Event notifying about this will also
     * get published. The request itself will get handled in the way that the configuration template specifies it.
     * @param configName the name of the configuration that should handle the SMS
     * @param params the request params coming from the provider
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{configName}")
    public void handleIncoming(@PathVariable String configName, @RequestParam Map<String, String> params) {
        LOGGER.info(String.format("Incoming SMS - configName = %s, params = %s", configName, params));

        Config config;
        if (configService.hasConfig(configName)) {
            config = configService.getConfig(configName);
        } else {
            String msg = String.format("Invalid config in incoming request: %s, params: %s", configName, params);
            LOGGER.error(msg);
            statusMessageService.warn(msg, SMS_MODULE);
            return;
        }
        Template template = templateService.getTemplate(config.getTemplateName());

        eventRelay.sendEventMessage(inboundEvent(config.getName(),
                getSender(params, template),
                getRecipient(params, template),
                getMessage(params, template),
                getMsgId(params, template),
                getTimestamp(params, template)));
        smsRecordsDataService.create(new SmsRecord(config.getName(),
                INBOUND,
                getSender(params, template),
                getMessage(params, template),
                now(),
                getStatus(params, template),
                null,
                null,
                getMsgId(params, template), null));
    }

    private String getSender(Map<String, String> params, Template template) {
        String sender = null;
        if (params.containsKey(template.getIncoming().getSenderKey())) {
            sender = params.get(template.getIncoming().getSenderKey());
            if (template.getIncoming().hasSenderRegex()) {
                sender = template.getIncoming().extractSender(sender);
            }
        }
        return sender;
    }

    private String getRecipient(Map<String, String> params, Template template) {
        String recipient = null;
        if (params.containsKey(template.getIncoming().getRecipientKey())) {
            recipient = params.get(template.getIncoming().getRecipientKey());
            if (template.getIncoming().hasRecipientRegex()) {
                recipient = template.getIncoming().extractRecipient(recipient);
            }
        }
        return recipient;
    }

    private String getMessage(Map<String, String> params, Template template) {
        return params.get(template.getIncoming().getMessageKey());
    }

    private String getMsgId(Map<String, String> params, Template template) {
        return params.get(template.getIncoming().getMsgIdKey());
    }

    private DateTime getTimestamp(Map<String, String> params, Template template) {
        if (params.containsKey(template.getIncoming().getTimestampKey())) {
            String dt = params.get(template.getIncoming().getTimestampKey());
            //todo: some providers may send timestamps in a different way, deal it it if/when we see that
            // replace "yyyy-mm-dd hh:mm:ss" with "yyyy-mm-ddThh:mm:ss" (note the T)
            if (dt.matches("(\\d\\d\\d\\d|\\d\\d)-\\d\\d?-\\d\\d? \\d\\d?:\\d\\d?:\\d\\d?")) {
                dt = dt.replace(" ", "T");
            }
            return DateTime.parse(dt);
        }
        return now();
    }

    private String getStatus(Map<String, String> params, Template template) {
        return template.getStatus().hasStatusKey() && params.containsKey(template.getStatus().getStatusKey()) ? params.get(template.getStatus().getStatusKey()) : DeliveryStatuses.RECEIVED;
    }
}
