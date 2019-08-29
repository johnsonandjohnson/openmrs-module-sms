package org.openmrs.module.sms.api.service;

import org.apache.commons.io.IOUtils;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sms.api.exception.ETLRuntimeException;
import org.openmrs.module.sms.api.util.Constants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service("etl.settings.manager")
public class SettingsManagerServiceImpl extends BaseOpenmrsService implements SettingsManagerService {

	@Override
	public void saveRawConfig(String configFileName, ByteArrayResource resource) {
		File destinationFile = getDestinationFile(configFileName);
		try (InputStream is = resource.getInputStream(); FileOutputStream fos = new FileOutputStream(destinationFile)) {
			IOUtils.copy(is, fos);
		} catch (IOException e) {
			throw new ETLRuntimeException("Error saving file " + configFileName, e);
		}
	}

	@Override
	public InputStream getRawConfig(String configFileName) {
		InputStream is = null;
		try {
			File configurationFile = getDestinationFile(configFileName);
			if (configurationFile.exists()) {
				is = new FileInputStream(configurationFile);
			}
		} catch (IOException e) {
			throw new ETLRuntimeException("Error loading file " + configFileName, e);
		}
		return is;
	}

	@Override
	public boolean configurationExist(String configurationFileName) {
		return getDestinationFile(configurationFileName).exists();
	}

	private File getDestinationFile(String filename) {
		File configFileFolder = OpenmrsUtil.getDirectoryInApplicationDataDirectory(Constants.CONFIG_DIR);
		return new File(configFileFolder, filename);
	}
}
