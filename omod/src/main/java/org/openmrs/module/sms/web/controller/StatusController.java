package org.openmrs.module.sms.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.audit.SmsAuditService;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.service.ConfigService;
import org.openmrs.module.sms.api.service.TemplateService;
import org.openmrs.module.sms.api.templates.Status;
import org.openmrs.module.sms.api.templates.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

/**
 * Handles message delivery status updates sent by sms providers to
 * {server}/openmrs/ws/sms/status{Config}
 */
@Controller
@RequestMapping(value = "/sms/status")
public class StatusController extends RestController {

  private static final Log LOGGER = LogFactory.getLog(StatusController.class);

  private SmsAuditService smsAuditService;
  private TemplateService templateService;
  private ConfigService configService;

  @Autowired
  public StatusController(
      @Qualifier("templateService") TemplateService templateService,
      @Qualifier("sms.configService") ConfigService configService,
      @Qualifier("sms.SmsAuditService") SmsAuditService smsAuditService) {
    this.templateService = templateService;
    this.configService = configService;
    this.smsAuditService = smsAuditService;
  }

  /**
   * Handles a status update from a provider. This method will result in publishing a OpenMRS Event
   * and creating a record in the database.
   *
   * @param configName the name of the configuration for the provider that is sending the update
   * @param params params of the request sent by the provider
   */
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @RequestMapping(value = "/{configName}", method = RequestMethod.POST)
  public void handle(
      @PathVariable String configName,
      @RequestParam Map<String, String> params,
      @RequestBody Map<String, Object> bodyParam) {
    LOGGER.info(
        String.format(
            "SMS Status - configName = %s, params = %s, bodyParam = %s",
            configName, params, bodyParam));

    if (!configService.hasConfig(configName)) {
      String msg =
          String.format(
              "Received SMS Status for '%s' config but no matching config: %s, "
                  + "will try the default config",
              configName, params);
      LOGGER.error(msg);
    }
    Config config = configService.getConfigOrDefault(configName);
    Template template = templateService.getTemplate(config.getTemplateName());
    Status status = template.getStatus();
    Map<String, String> combinedParams = getCombinedParams(params, bodyParam);

    if (status.hasMessageIdKey()
        && combinedParams != null
        && combinedParams.containsKey(status.getMessageIdKey())) {
      if (status.hasStatusKey() && status.hasStatusSuccess()) {
        smsAuditService.createOrUpdate(status, configName, combinedParams);
      } else {
        String msg =
            String.format(
                "We have a message id, but don't know how to extract message status, this is most likely a template error. Config: %s, Parameters: %s",
                configName, combinedParams);
        LOGGER.error(msg);
      }
    } else {
      String msg =
          String.format(
              "Status message received from provider, but no template support! Config: %s, Parameters: %s",
              configName, combinedParams);
      LOGGER.error(msg);
    }
  }
}
