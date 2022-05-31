/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
