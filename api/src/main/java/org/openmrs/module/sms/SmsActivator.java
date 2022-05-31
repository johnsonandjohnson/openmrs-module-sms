/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.sms;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.sms.api.event.AbstractSmsEventListener;
import org.openmrs.module.sms.api.event.SmsEventListenerFactory;
import org.openmrs.module.sms.api.service.IncomingMessageService;
import org.openmrs.module.sms.api.service.TemplateServiceImpl;
import org.openmrs.module.sms.api.util.SMSConstants;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class SmsActivator extends BaseModuleActivator implements DaemonTokenAware {

  private static final Log LOGGER = LogFactory.getLog(SmsActivator.class);

  @Override
  public void started() {
    LOGGER.info("Started Sms");
    createGlobalProperties();
    SmsEventListenerFactory.registerEventListeners();
    Context.getRegisteredComponent("templateService", TemplateServiceImpl.class).loadTemplates();
  }

  @Override
  public void stopped() {
    LOGGER.info("Stopped Sms");
    SmsEventListenerFactory.unRegisterEventListeners();
  }

  @Override
  public void setDaemonToken(DaemonToken daemonToken) {
    LOGGER.info("Set daemon token to SMS Module event listeners ans services");
    Context.getRegisteredComponents(AbstractSmsEventListener.class)
        .forEach(eventListener -> eventListener.setDaemonToken(daemonToken));
    Context.getRegisteredComponents(IncomingMessageService.class)
        .forEach(service -> service.setDaemonToken(daemonToken));
  }

  private void createGlobalProperties() {
    createGlobalSettingIfNotExists(
        SMSConstants.DEFAULT_USER_TIMEZONE,
        SMSConstants.DEFAULT_USER_TIMEZONE_DEFAULT_VALUE,
        SMSConstants.DEFAULT_USER_TIMEZONE_DESCRIPTION);
    createGlobalSettingIfNotExists(
        SMSConstants.GP_DISABLE_INCOMING_MESSAGE_HANDLERS,
        SMSConstants.GP_DISABLE_INCOMING_MESSAGE_HANDLERS_DEFAULT_VALUE,
        SMSConstants.GP_DISABLE_INCOMING_MESSAGE_HANDLERS_DESC);
    createGlobalSettingIfNotExists(
        SMSConstants.GP_SMS_VELOCITY_CONTEXT_SERVICE_MAP,
        SMSConstants.GP_SMS_VELOCITY_CONTEXT_SERVICE_MAP_DEFAULT_VALUE,
        SMSConstants.GP_SMS_VELOCITY_CONTEXT_SERVICE_MAP_DESC);
  }

  private void createGlobalSettingIfNotExists(String key, String value, String description) {
    String existingSetting = Context.getAdministrationService().getGlobalProperty(key);
    if (StringUtils.isBlank(existingSetting)) {
      GlobalProperty gp = new GlobalProperty(key, value, description);
      Context.getAdministrationService().saveGlobalProperty(gp);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            String.format("SMS Module created '%s' global property with value - %s", key, value));
      }
    }
  }
}
