/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.sms.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.audit.SmsAuditService;
import org.openmrs.module.sms.api.audit.SmsDirection;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.handler.IncomingMessageDataBuilder;
import org.openmrs.module.sms.api.service.ConfigService;
import org.openmrs.module.sms.api.service.IncomingMessageService;
import org.openmrs.module.sms.api.service.TemplateService;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.module.sms.api.util.IncomingMessageAccessor;
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
 * Handles http requests to {server}/openmrs/ws/sms/incoming{Config} sent by sms providers when they
 * receive an SMS
 */
@Controller
@RequestMapping(value = "/sms/incoming")
public class IncomingController extends RestController {

  private static final Log LOGGER = LogFactory.getLog(IncomingController.class);

  private TemplateService templateService;
  private ConfigService configService;
  private SmsAuditService smsAuditService;
  private IncomingMessageService incomingMessageService;

  @Autowired
  public IncomingController(
      @Qualifier("sms.SmsAuditService") SmsAuditService smsAuditService,
      @Qualifier("templateService") TemplateService templateService,
      @Qualifier("sms.configService") ConfigService configService,
      IncomingMessageService incomingMessageService) {
    this.smsAuditService = smsAuditService;
    this.templateService = templateService;
    this.configService = configService;
    this.incomingMessageService = incomingMessageService;
  }

  /**
   * Handles an incoming SMS notification coming from the provider. A OpenMRS Event notifying about
   * this will also get published. The request itself will get handled in the way that the
   * configuration template specifies it.
   *
   * @param configName the name of the configuration that should handle the SMS
   * @param params the request params coming from the provider
   */
  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/{configName}", method = RequestMethod.POST)
  public void handleIncoming(
      @PathVariable String configName,
      @RequestParam Map<String, String> params,
      @RequestBody Map<String, Object> bodyParam) {
    LOGGER.info(
        String.format(
            "Incoming SMS - configName = %s, params = %s, bodyParam = %s",
            configName, params, bodyParam));

    final Config config;
    if (configService.hasConfig(configName)) {
      config = configService.getConfig(configName);
    } else {
      String msg =
          String.format("Invalid config in incoming request: %s, params: %s", configName, params);
      LOGGER.error(msg);
      return;
    }

    final Template template = templateService.getTemplate(config.getTemplateName());
    final IncomingMessageAccessor messageAccessor =
        new IncomingMessageAccessor(template, getCombinedParams(params, bodyParam));

    final SmsRecord smsRecord =
        new SmsRecord(
            config.getName(),
            SmsDirection.INBOUND,
            messageAccessor.getSender(),
            messageAccessor.getMessage(),
            DateUtil.now(),
            messageAccessor.getStatus(),
            messageAccessor.getStatus(),
            null,
            messageAccessor.getMsgId(),
            null);

    LOGGER.info(String.format("SMS event - incoming message data = %s", smsRecord));
    final SmsRecord savedSmsRecord = smsAuditService.createOrUpdate(smsRecord);
    incomingMessageService.handleMessageSafe(
        new IncomingMessageDataBuilder(messageAccessor)
            .setReceivedForAFistTime(savedSmsRecord != null)
            .setReceivedAt(smsRecord.getTimestamp())
            .setConfig(config)
            .build());
  }
}
