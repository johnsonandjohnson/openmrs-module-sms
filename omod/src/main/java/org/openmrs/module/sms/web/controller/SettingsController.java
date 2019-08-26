package org.openmrs.module.sms.web.controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.config.SettingsFacade;
import org.openmrs.module.sms.api.configs.Configs;
import org.openmrs.module.sms.api.json.TemplateJsonParser;
import org.openmrs.module.sms.api.service.ConfigService;
import org.openmrs.module.sms.api.service.TemplateService;
import org.openmrs.module.sms.api.templates.TemplateForWeb;
import org.openmrs.module.sms.api.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

//todo: find a way to report useful information if encountering malformed templates?

/**
 * Sends templates to the UI, sends & receives configs to/from the UI.
 */
@Controller
@PreAuthorize(HAS_MANAGE_SMS_ROLE)
public class SettingsController {

    private static final Log LOGGER = LogFactory.getLog(SendController.class);

    private TemplateService templateService;
    private ConfigService configService;
    private TemplateJsonParser templateJsonParser;
    private SettingsFacade settingsFacade;

    /**
     * Returns all the templates for the UI.
     * @return a map of templates, keys are template names
     */
    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, TemplateForWeb> getTemplates() {
        return templateService.allTemplatesForWeb();
    }

    /**
     * Imports templates from the uploaded file.
     * @param file the file containing the templates
     * @throws IOException if there was a problem reading the file
     */
    @RequestMapping(value = "/templates/import", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void importTemplates(@RequestParam(value = "file") MultipartFile file)
            throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(file.getInputStream(), writer);

        templateJsonParser.importTemplates(writer.toString());
    }

    /**
     * Retrieves all configurations for the UI.
     * @return all configurations in the system
     */
    @RequestMapping(value = "/configs", method = RequestMethod.GET)
    @ResponseBody
    public Configs getConfigs() {
        return configService.getConfigs();
    }

    /**
     * Saves the provided configurations, overriding old ones.
     * @param configs all configurations to save
     * @return the newly saved configurations
     */
    @RequestMapping(value = "/configs", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Configs setConfigs(@RequestBody Configs configs) {
        configService.updateConfigs(configs);
        return configService.getConfigs();
    }

    /**
     * Handles exceptions, returns their message as the response body.
     * @param e the exception to handle
     * @return the exception message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        LOGGER.error("Error in SMS SettingsController", e);
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/mds-databrowser-config", method = RequestMethod.GET)
    @ResponseBody
    public String getCustomUISettings() throws IOException {
        return IOUtils.toString(settingsFacade.getRawConfig(Constants.UI_CONFIG));
    }

    @Autowired
    @Qualifier("templateService")
    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Autowired
    @Qualifier("configService")
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    @Autowired
    public void setTemplateJsonParser(TemplateJsonParser templateJsonParser) {
        this.templateJsonParser = templateJsonParser;
    }

    @Autowired
    public void setSettingsFacade(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }
}
