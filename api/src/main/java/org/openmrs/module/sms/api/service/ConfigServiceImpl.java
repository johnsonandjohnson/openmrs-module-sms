package org.openmrs.module.sms.api.service;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.configs.Configs;
import org.openmrs.module.sms.api.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/**
 * See {@link org.openmrs.module.sms.api.service.ConfigService}
 */
@Service("sms.configService")
public class ConfigServiceImpl extends BaseOpenmrsService implements ConfigService {

    private static final Log LOGGER = LogFactory.getLog(ConfigServiceImpl.class);

    @Autowired
    private SettingsManagerService settingsManagerService;
    private Configs configs;

    private synchronized void loadConfigs() {
        loadDefaultConfigurationIfNotExists(Constants.SMS_CONFIGS_FILE_NAME);
        try (InputStream is = settingsManagerService.getRawConfig(Constants.SMS_CONFIGS_FILE_NAME)) {
            String jsonText = IOUtils.toString(is);
            Gson gson = new Gson();
            configs = gson.fromJson(jsonText, Configs.class);
        }
        catch (Exception e) {
            throw new JsonIOException("Malformed " + Constants.SMS_CONFIGS_FILE_NAME + " file? " + e.toString(), e);
        }
    }

    @Autowired
    public ConfigServiceImpl(@Qualifier("sms.settings.manager") SettingsManagerService settingsManagerService) {
        this.settingsManagerService = settingsManagerService;
        loadConfigs();
    }

    public Config getDefaultConfig() {
        return configs.getDefaultConfig();
    }

    public List<Config> getConfigList() {
        return configs.getConfigs();
    }

    public Configs getConfigs() {
        return configs;
    }

    public boolean hasConfig(String name) {
        return configs.hasConfig(name);
    }

    public Config getConfig(String name) {
        return configs.getConfig(name);
    }

    public Config getConfigOrDefault(String name) {
        return configs.getConfigOrDefault(name);
    }

    public void updateConfigs(Configs configs) {
        Gson gson = new Gson();
        String jsonText = gson.toJson(configs, Configs.class);
        ByteArrayResource resource = new ByteArrayResource(jsonText.getBytes());
        settingsManagerService.saveRawConfig(Constants.SMS_CONFIGS_FILE_NAME, resource);
        loadConfigs();
    }

    public boolean hasConfigs() {
        return !configs.isEmpty();
    }

    @Override
    public String getServerUrl() {
        String serverUrl = Context.getAdministrationService().getGlobalProperty(Constants.SMS_SERVER_URL);

        if (StringUtils.isEmpty(serverUrl)) {
            String message = String.format("The %s global setting need to be set. Default sms server url has been " +
                    "used: %s", Constants.SMS_SERVER_URL, Constants.DEFAULT_SMS_SERVER_URL);
            LOGGER.warn(message);
            serverUrl = Constants.DEFAULT_SMS_SERVER_URL;
        }
        return serverUrl;
    }

    private void loadDefaultConfigurationIfNotExists(String filename) {
        if (settingsManagerService.configurationNotExist(filename)) {
            settingsManagerService.crateConfigurationFromResources(filename);
        }
    }
}
