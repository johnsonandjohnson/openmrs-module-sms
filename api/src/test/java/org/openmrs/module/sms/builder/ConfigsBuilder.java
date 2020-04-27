package org.openmrs.module.sms.builder;

import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.configs.Configs;

import java.util.Collections;
import java.util.List;

public class ConfigsBuilder extends AbstractBuilder<Configs> {

    private List<Config> configs;
    private String defaultConfigName;

    public ConfigsBuilder() {
        Config config = new ConfigBuilder().buildAsNew();
        this.configs = Collections.singletonList(config);
        this.defaultConfigName = config.getName();
    }

    @Override
    public Configs build() {
        Configs configurations = new Configs();
        configurations.setConfigs(this.configs);
        configurations.setDefaultConfigName(this.defaultConfigName);
        return configurations;
    }

    @Override
    public Configs buildAsNew() {
        return build();
    }

    public List<Config> getConfigs() {
        return configs;
    }

    public ConfigsBuilder withConfigs(List<Config> configs) {
        this.configs = configs;
        return this;
    }

    public String getDefaultConfigName() {
        return defaultConfigName;
    }

    public ConfigsBuilder withDefaultConfigName(String defaultConfigName) {
        this.defaultConfigName = defaultConfigName;
        return this;
    }
}
