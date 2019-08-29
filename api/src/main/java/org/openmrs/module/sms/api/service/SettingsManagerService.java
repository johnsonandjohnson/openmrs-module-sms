package org.openmrs.module.sms.api.service;

import org.openmrs.api.OpenmrsService;
import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;

public interface SettingsManagerService extends OpenmrsService {

	void saveRawConfig(String configFileName, ByteArrayResource resource);

	InputStream getRawConfig(String configFileName);

	boolean configurationExist(String configurationFileName);

}