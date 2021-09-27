package org.openmrs.module.sms.api.service;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sms.api.configs.Configs;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.templates.TemplateForWeb;
import org.openmrs.module.sms.api.util.ResourceUtil;
import org.openmrs.module.sms.builder.ConfigsBuilder;
import org.openmrs.module.sms.builder.TemplateForWebBuilder;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SmsSettingsServiceImplTest extends BaseModuleContextSensitiveTest {

    private static final int FIRST_ELEMENT = 0;
    private static final int SECOND_ELEMENT = 1;
    private static final int THIRD_ELEMENT = 2;
    private static final String SMS_TEMPLATES_JSON = "sms-templates.json";
    private static final int EXPECTED_EMPTY_CONFIGS_SIZE = 0;
    private static final int EXPECTED_CONFIGS_SIZE = 3;

    @Autowired
    @Qualifier("sms.SettingsService")
    private SmsSettingsService smsSettingsService;

    @Autowired
    @Qualifier("templateService")
    private TemplateService templateService;

    @Autowired
    @Qualifier("sms.configService")
    private ConfigService configService;

    @Before
    public void setUp() {
        Configs configs = new ConfigsBuilder().buildAsNew();
        configService.updateConfigs(configs);
        templateService.loadTemplates();
    }

    @Test
    public void getTemplatesShouldReturnExpectedResult() {
        Map<String, TemplateForWeb> expected = new HashMap<>();
        expected.put("nexmo", new TemplateForWebBuilder().build());
        Map<String, TemplateForWeb> actual = smsSettingsService.getTemplates();
        for (Map.Entry<String, TemplateForWeb> entry : expected.entrySet()) {
            TemplateForWeb expectedTemplate = entry.getValue();
            TemplateForWeb actualTemplate = actual.get(entry.getKey());
            assertThat(actualTemplate.getName(), is(expectedTemplate.getName()));
            assertThat(actualTemplate.getConfigurables().get(FIRST_ELEMENT),
                    is(expectedTemplate.getConfigurables().get(FIRST_ELEMENT)));
            assertThat(actualTemplate.getConfigurables().get(SECOND_ELEMENT),
                    is(expectedTemplate.getConfigurables().get(SECOND_ELEMENT)));
            assertThat(actualTemplate.getConfigurables().get(THIRD_ELEMENT),
                    is(expectedTemplate.getConfigurables().get(THIRD_ELEMENT)));
        }
    }

    @Test
    public void importTemplatesSuccessfully() {
        // clear template service
        ReflectionTestUtils.setField(templateService, "templates", new HashMap<>());
        String templateName = "nexmo";
        assertThatMissing(templateName);
        String jsonText = ResourceUtil.readResourceFile(SMS_TEMPLATES_JSON);
        smsSettingsService.importTemplates(jsonText);
        Template template = templateService.getTemplate(templateName);
        assertThat(template, is(notNullValue()));
        assertThat(template.toString(), is(notNullValue()));
        assertThat(template.getName(), is(templateName));
    }

    @Test
    public void getConfigsShouldReturnExpectedResult() {
        Configs expected = new Configs();
        configService.updateConfigs(expected);
        Configs actual = smsSettingsService.getConfigs();
        assertThat(actual.getConfigs().size(), is(EXPECTED_EMPTY_CONFIGS_SIZE));

        Configs configs = new ConfigsBuilder().buildAsNew();
        smsSettingsService.setConfigs(configs);
        Configs actualAfterChange = smsSettingsService.getConfigs();
        assertThat(actualAfterChange.getConfigs().size(), is(EXPECTED_CONFIGS_SIZE));
    }

    private void assertThatMissing(String configName) {
        boolean thrown = false;
        try {
            templateService.getTemplate(configName);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }
}