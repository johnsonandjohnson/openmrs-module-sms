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
import org.openmrs.module.sms.api.util.Constants;

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
        Constants.DEFAULT_USER_TIMEZONE,
        Constants.DEFAULT_USER_TIMEZONE_DEFAULT_VALUE,
        Constants.DEFAULT_USER_TIMEZONE_DESCRIPTION);
    createGlobalSettingIfNotExists(
        Constants.GP_DISABLE_INCOMING_MESSAGE_HANDLERS,
        Constants.GP_DISABLE_INCOMING_MESSAGE_HANDLERS_DEFAULT_VALUE,
        Constants.GP_DISABLE_INCOMING_MESSAGE_HANDLERS_DESC);
    createGlobalSettingIfNotExists(
        Constants.GP_SMS_VELOCITY_CONTEXT_SERVICE_MAP,
        Constants.GP_SMS_VELOCITY_CONTEXT_SERVICE_MAP_DEFAULT_VALUE,
        Constants.GP_SMS_VELOCITY_CONTEXT_SERVICE_MAP_DESC);
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
