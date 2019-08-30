package org.openmrs.module.sms.api.service;

import org.apache.commons.io.IOUtils;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sms.api.configs.Configs;
import org.openmrs.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.module.sms.api.json.TemplateJsonParser;
import org.openmrs.module.sms.api.templates.TemplateForWeb;
import org.openmrs.module.sms.api.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Map;

public class SmsSettingsServiceImpl extends BaseOpenmrsService implements SmsSettingsService {

	@Autowired
	@Qualifier("templateService")
	private TemplateService templateService;

	@Autowired
	@Qualifier("configService")
	private ConfigService configService;

	@Autowired
	private TemplateJsonParser templateJsonParser;

	@Autowired
	private SettingsManagerService settingsManagerService;

	@Override
	public Map<String, TemplateForWeb> getTemplates() {
		return templateService.allTemplatesForWeb();
	}

	@Override
	public void importTemplates(String templates) {
		templateJsonParser.importTemplates(templates);
	}

	@Override
	public Configs getConfigs() {
		return configService.getConfigs();
	}

	@Override
	public Configs setConfigs(Configs configs) {
		configService.updateConfigs(configs);
		return configService.getConfigs();
	}

	@Override
	public String getCustomUISettings() {
		try {
			return IOUtils.toString(settingsManagerService.getRawConfig(Constants.UI_CONFIG));
		} catch (IOException e) {
			throw new SmsRuntimeException(e);
		}
	}
}
