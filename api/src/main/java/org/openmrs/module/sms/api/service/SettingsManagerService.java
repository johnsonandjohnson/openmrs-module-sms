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

import org.openmrs.api.OpenmrsService;
import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;

public interface SettingsManagerService extends OpenmrsService {

    void saveRawConfig(String configFileName, ByteArrayResource resource);

    InputStream getRawConfig(String configFileName);

    boolean configurationExist(String configurationFileName);

    void createEmptyConfiguration(String fileName);

    void createConfigurationFromResources(String fileName);
}
