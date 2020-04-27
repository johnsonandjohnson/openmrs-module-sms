package org.openmrs.module.sms.builder;

import org.openmrs.module.sms.api.configs.ConfigProp;

public class ConfigPropBuilder extends AbstractBuilder<ConfigProp> {

    private String name;
    private String value;

    public ConfigPropBuilder() {
        this.name = "from";
        this.value = "48777666222";
    }

    @Override
    public ConfigProp build() {
        ConfigProp configProp = new ConfigProp();
        configProp.setName(this.name);
        configProp.setValue(this.value);
        return configProp;
    }

    @Override
    public ConfigProp buildAsNew() {
        return build();
    }

    public String getName() {
        return name;
    }

    public ConfigPropBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ConfigPropBuilder withValue(String value) {
        this.value = value;
        return this;
    }
}
