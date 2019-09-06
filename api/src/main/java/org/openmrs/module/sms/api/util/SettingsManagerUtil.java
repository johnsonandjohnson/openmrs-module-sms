package org.openmrs.module.sms.api.util;

import org.openmrs.api.context.Context;
import org.openmrs.module.sms.api.service.SettingsManagerService;
import org.springframework.core.io.ByteArrayResource;

public final class SettingsManagerUtil {

	public static SettingsManagerService getSettingsManagerService() {
		return Context.getRegisteredComponent("sms.settings.manager", SettingsManagerService.class);
	}

	public static boolean configurationNotExist(String filename) {
		return !getSettingsManagerService().configurationExist(filename);
	}

	public static void createEmptyConfiguration(String filename) {
		ByteArrayResource resource = new ByteArrayResource("".getBytes());
		getSettingsManagerService().saveRawConfig(filename, resource);
	}

	public static void loadDefaultConfigurationFromResources(String filename) {
		String defaultConfiguration = ResourceUtil.readResourceFile(filename);
		ByteArrayResource resource = new ByteArrayResource(defaultConfiguration.getBytes());
		getSettingsManagerService().saveRawConfig(filename, resource);
	}

	public static void createEmptyIfNotExists(String filename) {
		if (configurationNotExist(filename)){
			createEmptyConfiguration(filename);
		}
	}

	public static void loadDefaultIfNotExists(String filename) {
		if (configurationNotExist(filename)){
			loadDefaultConfigurationFromResources(filename);
		}
	}

	public static void tryLoadDefaultOrCreateEmptyIfNotExists(String filename) {
		if (configurationNotExist(filename)){
			if (ResourceUtil.resourceFileExists(filename)) {
				loadDefaultConfigurationFromResources(filename);
			} else {
				createEmptyConfiguration(filename);
			}
		}
	}

	private SettingsManagerUtil() { }
}
