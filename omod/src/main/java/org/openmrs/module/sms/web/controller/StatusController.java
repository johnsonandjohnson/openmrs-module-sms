package org.openmrs.module.sms.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Order;
import org.openmrs.module.sms.api.audit.SmsAuditService;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.SmsRecordSearchCriteria;
import org.openmrs.module.sms.api.audit.SmsRecords;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatuses;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.api.service.ConfigService;
import org.openmrs.module.sms.api.service.TemplateService;
import org.openmrs.module.sms.api.templates.Status;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.notification.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

import static org.openmrs.module.sms.api.audit.SmsDirection.OUTBOUND;

/**
 * Handles message delivery status updates sent by sms providers to
 * {server}/openmrs/ws/sms/status{Config}
 */
@Controller
@RequestMapping(value = "/sms/status")
public class StatusController {

    private static final int RECORD_FIND_RETRY_COUNT = 3;
    private static final int RECORD_FIND_TIMEOUT = 500;
    private static final String SMS_MODULE = "openmrs-sms";

    private static final Log LOGGER = LogFactory.getLog(StatusController.class);
    private static final String NOTIFICATION_TEMPLATE = "%s - %s";

    private AlertService alertService;
    private SmsAuditService smsAuditService;
    private TemplateService templateService;
    private ConfigService configService;
    private SmsRecordDao smsRecordDao;

    @Autowired
    public StatusController(
            @Qualifier("templateService") TemplateService templateService,
            @Qualifier("sms.configService") ConfigService configService,
            @Qualifier("alertService") AlertService alertService,
            @Qualifier("smsAuditService") SmsAuditService smsAuditService,
            @Qualifier("sms.SmsRecordDao") SmsRecordDao smsRecordDao) {
        this.templateService = templateService;
        this.configService = configService;
        this.alertService = alertService;
        this.smsAuditService = smsAuditService;
        this.smsRecordDao = smsRecordDao;
    }

    /**
     * Handles a status update from a provider. This method will result in publishing a OpenMRS Event and creating
     * a record in the database.
     *
     * @param configName the name of the configuration for the provider that is sending the update
     * @param params     params of the request sent by the provider
     */
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{configName}", method = RequestMethod.GET)
    public void handle(@PathVariable String configName, @RequestParam Map<String, String> params) {
        LOGGER.info(String.format("SMS Status - configName = %s, params = %s", configName, params));

        if (!configService.hasConfig(configName)) {
            String msg = String.format("Received SMS Status for '%s' config but no matching config: %s, " +
                    "will try the default config", configName, params);
            LOGGER.error(msg);
            alertService.notifySuperUsers(String.format(NOTIFICATION_TEMPLATE, SMS_MODULE, msg), null);
        }
        Config config = configService.getConfigOrDefault(configName);
        Template template = templateService.getTemplate(config.getTemplateName());
        Status status = template.getStatus();

        if (status.hasMessageIdKey() && params != null && params.containsKey(status.getMessageIdKey())) {
            if (status.hasStatusKey() && status.hasStatusSuccess()) {
                analyzeStatus(status, configName, params);
            } else {
                String msg = String.format("We have a message id, but don't know how to extract message status, this is most likely a template error. Config: %s, Parameters: %s",
                        configName, params);
                LOGGER.error(msg);
                alertService.notifySuperUsers(String.format(NOTIFICATION_TEMPLATE, SMS_MODULE, msg), null);
            }
        } else {
            String msg = String.format("Status message received from provider, but no template support! Config: %s, Parameters: %s",
                    configName, params);
            LOGGER.error(msg);
            alertService.notifySuperUsers(String.format(NOTIFICATION_TEMPLATE, SMS_MODULE, msg), null);
        }
    }

    private SmsRecord findFirstByProviderMessageId(SmsRecords smsRecords, String providerMessageId) {
        for (SmsRecord smsRecord : smsRecords.getRecords()) {
            if (smsRecord.getProviderId().equals(providerMessageId)) {
                return smsRecord;
            }
        }
        return null;
    }

    private SmsRecord findOrCreateSmsRecord(String configName, String providerMessageId, String statusString) {
        int retry = 0;
        SmsRecord smsRecord;
        SmsRecords smsRecords;
        SmsRecord existingSmsRecord = null;
        Order order = Order.desc("timestamp");

        // Try to find an existing SMS record using the provider message ID
        // NOTE: Only works if the provider guarantees the message id is unique. So far, all do.
        do {
            //seems that lucene takes a while to index, so try a couple of times and delay in between
            if (retry > 0) {
                LOGGER.debug(String.format("Trying again to find log record with openMrsId %s, try %d",
                        providerMessageId, retry + 1));
                try {
                    Thread.sleep(RECORD_FIND_TIMEOUT);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            smsRecords = smsAuditService.findAllSmsRecords(new SmsRecordSearchCriteria()
                    .withConfig(configName)
                    .withProviderId(providerMessageId)
                    .withOrder(order));
            retry++;
        } while (retry < RECORD_FIND_RETRY_COUNT && CollectionUtils.isEmpty(smsRecords.getRecords()));

        if (CollectionUtils.isEmpty(smsRecords.getRecords())) {
            // If we couldn't find a record by provider message ID try using the OPENMRS ID
            smsRecords = smsAuditService.findAllSmsRecords(new SmsRecordSearchCriteria()
                    .withConfig(configName)
                    .withOpenMrsId(providerMessageId)
                    .withOrder(order));
            if (!CollectionUtils.isEmpty(smsRecords.getRecords())) {
                LOGGER.debug(String.format("Found log record with matching openMrsId %s", providerMessageId));
                existingSmsRecord = smsRecords.getRecords().get(0);
            }
        } else {
            //todo: temporary kludge: lucene can't find exact strings, so we're looping on results until we find
            //todo: an exact match. Remove when we switch to SEUSS.
            existingSmsRecord = findFirstByProviderMessageId(smsRecords, providerMessageId);
            if (existingSmsRecord != null) {
                LOGGER.debug(String.format("Found log record with matching providerId %s", providerMessageId));
            }
        }

        if (existingSmsRecord == null) {
            String msg = String.format("Received status update but couldn't find a log record with matching " +
                    "ProviderMessageId or openMrsId: %s", providerMessageId);
            LOGGER.error(msg);
            alertService.notifySuperUsers(String.format(NOTIFICATION_TEMPLATE, SMS_MODULE, msg), null);
        }

        smsRecord = getSmsRecord(configName, providerMessageId, statusString, existingSmsRecord);

        return smsRecord;
    }

    private SmsRecord getSmsRecord(String configName, String providerMessageId, String statusString,
                                   SmsRecord existingSmsRecord) {
        SmsRecord smsRecord;
        if (existingSmsRecord != null) {
            smsRecord = new SmsRecord(configName, OUTBOUND, existingSmsRecord.getPhoneNumber(),
                    existingSmsRecord.getMessageContent(), DateUtil.now(), null, statusString,
                    existingSmsRecord.getOpenMrsId(), providerMessageId, null);
        } else {
            //start with an empty SMS record
            smsRecord = new SmsRecord(configName, OUTBOUND, null, null, DateUtil.now(),
                    null, statusString, null,
                    providerMessageId, null);
        }
        return smsRecord;
    }

    private void analyzeStatus(Status status, String configName, Map<String, String> params) {
        String statusString = params.get(status.getStatusKey());
        String providerMessageId = params.get(status.getMessageIdKey());
        SmsRecord smsRecord = findOrCreateSmsRecord(configName, providerMessageId, statusString);

        if (statusString != null) {
            smsRecord.setDeliveryStatus(statusString);
        } else {
            String msg = String.format("Likely template error, unable to extract status string. Config: %s, Parameters: %s",
                    configName, params);
            LOGGER.error(msg);
            alertService.notifySuperUsers(String.format(NOTIFICATION_TEMPLATE, SMS_MODULE, msg), null);
            smsRecord.setDeliveryStatus(DeliveryStatuses.FAILURE_CONFIRMED);
        }
        smsRecordDao.create(smsRecord);
    }
}
