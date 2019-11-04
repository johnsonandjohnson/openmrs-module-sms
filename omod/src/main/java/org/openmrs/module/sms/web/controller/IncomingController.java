package org.openmrs.module.sms.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatuses;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.api.service.ConfigService;
import org.openmrs.module.sms.api.service.TemplateService;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.notification.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;
import java.util.Map;

import static org.openmrs.module.sms.api.audit.SmsDirection.INBOUND;

/**
 * Handles http requests to {server}/openmrs/ws/sms/incoming{Config} sent by sms providers
 * when they receive an SMS
 */
@Controller
@RequestMapping(value = "/sms/incoming")
public class IncomingController {

    private static final String SMS_MODULE = "openmrs-sms";

    private static final Log LOGGER = LogFactory.getLog(IncomingController.class);

    private TemplateService templateService;
    private ConfigService configService;
    private SmsRecordDao smsRecordDao;
    private AlertService alertService;


    @Autowired
    public IncomingController(@Qualifier("sms.SmsRecordDao") SmsRecordDao smsRecordDao,
            @Qualifier("templateService") TemplateService templateService,
            @Qualifier("sms.configService") ConfigService configService,
            @Qualifier("alertService") AlertService alertService) {
        this.smsRecordDao = smsRecordDao;
        this.templateService = templateService;
        this.configService = configService;
        this.alertService = alertService;
    }


    //todo: add provider-specific UI to explain how implementers must setup their providers' incoming callback

    /**
     * Handles an incoming SMS notification coming from the provider. A OpenMRS Event notifying about this will also
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
            alertService.notifySuperUsers(String.format("%s - %s", SMS_MODULE, msg), null);
            return;
        }
        Template template = templateService.getTemplate(config.getTemplateName());

        smsRecordDao.create(new SmsRecord(config.getName(),
                INBOUND,
                getSender(params, template),
                getMessage(params, template),
                DateUtil.now(),
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

    private Date getTimestamp(Map<String, String> params, Template template) {
        if (params.containsKey(template.getIncoming().getTimestampKey())) {
            String dt = params.get(template.getIncoming().getTimestampKey());
            //todo: some providers may send timestamps in a different way, deal it it if/when we see that
            // replace "yyyy-mm-dd hh:mm:ss" with "yyyy-mm-ddThh:mm:ss" (note the T)
            if (dt.matches("(\\d\\d\\d\\d|\\d\\d)-\\d\\d?-\\d\\d? \\d\\d?:\\d\\d?:\\d\\d?")) {
                dt = dt.replace(" ", "T");
            }
            return DateUtil.parse(dt);
        }
        return DateUtil.now();
    }

    private String getStatus(Map<String, String> params, Template template) {
        return template.getStatus().hasStatusKey() && params.containsKey(template.getStatus().getStatusKey()) ? params.get(template.getStatus().getStatusKey()) : DeliveryStatuses.RECEIVED;
    }
}
