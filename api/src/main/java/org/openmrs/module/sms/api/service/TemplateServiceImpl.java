package org.openmrs.module.sms.api.service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.templates.TemplateForWeb;
import org.openmrs.module.sms.api.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * See {@link org.openmrs.module.sms.api.service.TemplateService}.
 * This implementation uses the MOTECH configuration system to store the templates.
 */
@Service("templateService")
public class TemplateServiceImpl implements TemplateService {

    private static final String SMS_TEMPLATE_CUSTOM_FILE_NAME = "sms-templates-custom.json";
    private static final String SMS_TEMPLATE_FILE_NAME = "sms-templates.json";
    private static final Log LOGGER = LogFactory.getLog(TemplateServiceImpl.class);
    private SettingsManagerService settingsManagerService;
    private Map<String, Template> templates = new HashMap<>();

    @Override
    public Template getTemplate(String name) {
        if (templates.containsKey(name)) {
            return templates.get(name);
        }
        throw new IllegalArgumentException(String.format("Unknown template: '%s'.", name));
    }

    @Override
    public Map<String, TemplateForWeb> allTemplatesForWeb() {
        Map<String, TemplateForWeb> ret = new HashMap<>();
        for (Map.Entry<String, Template> entry : templates.entrySet()) {
            ret.put(entry.getKey(), new TemplateForWeb(entry.getValue()));
        }
        return ret;
    }

    @Override
    public void importTemplates(List<Template> templateList) {
        for (Template template : templateList) {
            importTemplate(template);
        }

        Gson gson = new Gson();
        String jsonText = gson.toJson(templateList, new TypeToken<List<Template>>() { } .getType());
        settingsManagerService.saveRawConfig(SMS_TEMPLATE_CUSTOM_FILE_NAME, new ByteArrayResource(jsonText.getBytes()));
    }

    @Override
    public void importTemplate(Template template) {
        template.readDefaults();
        templates.put(template.getName(), template);
    }

    @Autowired
    public TemplateServiceImpl(@Qualifier("sms.settings.manager") SettingsManagerService settingsManagerService) {
        this.settingsManagerService = settingsManagerService;
        loadTemplates();
    }

    private synchronized void loadTemplates() {
        templates = new HashMap<>();
        load(SMS_TEMPLATE_FILE_NAME);
        load(SMS_TEMPLATE_CUSTOM_FILE_NAME);
    }

    private void load(String fileName) {
        List<Template> templateList = new ArrayList<>();
        tryLoadDefaultOrCreateEmptyConfigurationIfNotExists(fileName);
        try (InputStream is = settingsManagerService.getRawConfig(fileName)) {
            String jsonText = IOUtils.toString(is);
            Gson gson = new Gson();
            if (StringUtils.isNotBlank(jsonText)) {
                 templateList = gson.fromJson(jsonText, new TypeToken<List<Template>>() {}.getType());
            }
        } catch (JsonParseException e) {
            throw new SmsRuntimeException("File " + fileName + " is malformed", e);
        } catch (IOException e) {
            throw new SmsRuntimeException("Error loading file " + fileName, e);
        }

        for (Template template : templateList) {
            template.readDefaults();
            templates.put(template.getName(), template);
        }
    }

    private void tryLoadDefaultOrCreateEmptyConfigurationIfNotExists(String filename) {
        if (settingsManagerService.configurationNotExist(filename)) {
            if (ResourceUtil.resourceFileExists(filename)) {
                settingsManagerService.createConfigurationFromResources(filename);
            } else {
                settingsManagerService.createEmptyConfiguration(filename);
            }
        }
    }
}
