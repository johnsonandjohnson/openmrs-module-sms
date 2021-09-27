package org.openmrs.module.sms.builder;

import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.templates.TemplateForWeb;

import java.util.ArrayList;
import java.util.List;

public class TemplateForWebBuilder extends AbstractBuilder<TemplateForWeb> {

    private String name;
    private List<String> configurables;

    public TemplateForWebBuilder() {
        this.name = "nexmo";
        this.configurables = buildConfigurables();
    }

    @Override
    public TemplateForWeb build() {
        TemplateForWeb templateForWeb = new TemplateForWeb(new Template());
        templateForWeb.setName(this.name);
        templateForWeb.setConfigurables(this.configurables);
        return templateForWeb;
    }

    @Override
    public TemplateForWeb buildAsNew() {
        return build();
    }

    public String getName() {
        return name;
    }

    public TemplateForWebBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getConfigurables() {
        return configurables;
    }

    public TemplateForWebBuilder withConfigurables(List<String> configurables) {
        this.configurables = configurables;
        return this;
    }

    private List<String> buildConfigurables() {
        List<String> result = new ArrayList<>();
        result.add("from");
        result.add("api_key");
        result.add("api_secret");
        return result;
    }
}
