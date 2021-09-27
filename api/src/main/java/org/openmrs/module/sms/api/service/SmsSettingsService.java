package org.openmrs.module.sms.api.service;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sms.api.configs.Configs;
import org.openmrs.module.sms.api.templates.TemplateForWeb;
import org.openmrs.module.sms.api.util.PrivilegeConstants;

import java.util.Map;

public interface SmsSettingsService extends OpenmrsService {

    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    Map<String, TemplateForWeb> getTemplates();

    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    void importTemplates(String templates);

    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    Configs getConfigs();

    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    Configs setConfigs(Configs configs);

    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    String getCustomUISettings();
}
