package org.openmrs.module.sms.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.audit.SmsAuditService;
import org.openmrs.module.sms.api.audit.SmsDirection;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.configs.Config;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

/**
 * Handles http requests to {server}/openmrs/ws/sms/incoming{Config} sent by sms providers
 * when they receive an SMS
 */
@Controller
@RequestMapping(value = "/sms/incoming")
public class IncomingController extends RestController {

    private static final String SMS_MODULE = "openmrs-sms";

    private static final Log LOGGER = LogFactory.getLog(IncomingController.class);

    private TemplateService templateService;
    private ConfigService configService;
    private AlertService alertService;
    private SmsAuditService smsAuditService;

    @Autowired
    public IncomingController(@Qualifier("sms.SmsAuditService") SmsAuditService smsAuditService,
                              @Qualifier("templateService") TemplateService templateService,
                              @Qualifier("sms.configService") ConfigService configService,
                              @Qualifier("alertService") AlertService alertService) {
        this.smsAuditService = smsAuditService;
        this.templateService = templateService;
        this.configService = configService;
        this.alertService = alertService;
    }

    //todo: add provider-specific UI to explain how implementers must setup their providers' incoming callback

    /**
     * Handles an incoming SMS notification coming from the provider. A OpenMRS Event notifying about this will also
     * get published. The request itself will get handled in the way that the configuration template specifies it.
     *
     * @param configName the name of the configuration that should handle the SMS
     * @param params     the request params coming from the provider
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{configName}", method = RequestMethod.POST)
    public void handleIncoming(@PathVariable String configName, @RequestParam Map<String, String> params,
                               @RequestBody Map<String, Object> bodyParam) {
        LOGGER.info(String.format("Incoming SMS - configName = %s, params = %s, bodyParam = %s",
                configName, params, bodyParam));

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
        Map<String, String> combinedParams = getCombinedParams(params, bodyParam);

        SmsRecord smsRecord = new SmsRecord(config.getName(), SmsDirection.INBOUND, getSender(combinedParams, template),
                getMessage(combinedParams, template), DateUtil.now(), getStatus(combinedParams, template),
                getStatus(combinedParams, template), null, getMsgId(combinedParams, template), null);
        smsAuditService.createOrUpdate(smsRecord);
    }
}
