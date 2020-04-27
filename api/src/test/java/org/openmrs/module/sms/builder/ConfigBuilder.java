package org.openmrs.module.sms.builder;

import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.configs.ConfigProp;

import java.util.ArrayList;
import java.util.List;

public class ConfigBuilder extends AbstractBuilder<Config> {

    private String name;
    private Integer maxRetries;
    private Boolean excludeLastFooter;
    private String splitHeader;
    private String splitFooter;
    private String templateName;
    private List<ConfigProp> props;

    public ConfigBuilder() {
        this.name = "nexmo";
        this.maxRetries = 3;
        this.excludeLastFooter = true;
        this.splitHeader = "Msg $m of $t";
        this.splitFooter = "...";
        this.templateName = "nexmo";
        this.props = new ArrayList<>();
        this.props.add(new ConfigPropBuilder()
                .withName("from")
                .withValue("48223551002")
                .build());
        this.props.add(new ConfigPropBuilder()
                .withName("api_key")
                .withValue("YOUR_API_KEY")
                .build());
        this.props.add(new ConfigPropBuilder()
                .withName("api_secret")
                .withValue("YOUR_API_SECRET")
                .build());
    }

    @Override
    public Config build() {
        Config config = new Config();
        config.setName(this.name);
        config.setMaxRetries(this.maxRetries);
        config.setExcludeLastFooter(this.excludeLastFooter);
        config.setSplitHeader(this.splitHeader);
        config.setSplitFooter(this.splitFooter);
        config.setTemplateName(this.templateName);
        config.setProps(this.props);
        return config;
    }

    @Override
    public Config buildAsNew() {
        return build();
    }
}
