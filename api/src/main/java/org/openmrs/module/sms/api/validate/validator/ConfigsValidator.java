package org.openmrs.module.sms.api.validate.validator;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.configs.Configs;
import org.openmrs.module.sms.api.service.ConfigService;
import org.openmrs.module.sms.api.validate.annotation.ValidConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ConfigsValidator implements ConstraintValidator<ValidConfigs, Configs> {

    private static final String NAME_PATH = "configs[%d].name";
    private static final String TEMPLATE_PATH = "configs[%d].template";
    private static final String MAX_RETRIES_PATH = "configs[%d].maxRetries";
    private static final String SPLIT_HEADER_PATH = "configs[%d].splitHeader";
    private static final String SPLIT_FOOTER_PATH = "configs[%d].splitFooter";
    private static final Pattern ALPHA_NUMERIC = Pattern.compile("^[a-zA-Z0-9]+$");

    @Autowired
    private ConfigService configService;

    @Override
    public void initialize(ValidConfigs validConfigs) {

    }

    /**
     * Validates the name uniqueness.
     *
     * @param configs object which wraps configs to validate
     * @param ctx     validation context
     * @return the validation result
     */
    @Override
    public boolean isValid(Configs configs, ConstraintValidatorContext ctx) {
        ctx.disableDefaultConstraintViolation();

        boolean isValid = true;
        List<Config> configList = configs.getConfigs();
        for (int i = 0; i < configList.size(); i++) {
            Config config = configList.get(i);
            isValid = isNameValid(config, i, configList, ctx) && isValid;
            isValid = isTemplateValid(config, i, ctx) && isValid;
            isValid = isMaxRetriesValid(config, i, ctx) && isValid;
            isValid = isSplitHeaderValid(config, i, ctx) && isValid;
            isValid = isSplitFooterValid(config, i, ctx) && isValid;
        }

        return isValid;
    }

    private boolean isNameValid(Config validatedConfig, int index, List<Config> configList, ConstraintValidatorContext ctx) {
        boolean isValid = true;
        if (StringUtils.isEmpty(validatedConfig.getName())
                || !ALPHA_NUMERIC.matcher(validatedConfig.getName()).matches()) {
            addErrorToContext(ctx, getPath(NAME_PATH, index), "Invalid config name");
            isValid = false;
        }
        for (Config config : configList) {
            if (validatedConfig != config
                    && StringUtils.equals(validatedConfig.getName(), config.getName())) {
                addErrorToContext(ctx, getPath(NAME_PATH, index), String.format("%s name is duplicated",
                        validatedConfig.getName()));
                isValid = false;
            }
        }

        return isValid;
    }

    private boolean isTemplateValid(Config config, int index, ConstraintValidatorContext ctx) {
        if (StringUtils.isBlank(config.getTemplateName())) {
            addErrorToContext(ctx, getPath(TEMPLATE_PATH, index), "Template name not set");
            return false;
        }
        return true;
    }

    private boolean isMaxRetriesValid(Config config, int index, ConstraintValidatorContext ctx) {
        if (config.getMaxRetries() == null) {
            addErrorToContext(ctx, getPath(MAX_RETRIES_PATH, index), "Max retries not set");
            return false;
        }

        if (config.getMaxRetries() < 0) {
            addErrorToContext(ctx, getPath(MAX_RETRIES_PATH, index), "Max retries number cannot be negative");
            return false;
        }
        return true;
    }

    private boolean isSplitHeaderValid(Config config, int index, ConstraintValidatorContext ctx) {
        if (StringUtils.isBlank(config.getSplitHeader())) {
            addErrorToContext(ctx, getPath(SPLIT_HEADER_PATH, index), "Split header not set");
            return false;
        }
        return true;
    }

    private boolean isSplitFooterValid(Config config, int index, ConstraintValidatorContext ctx) {
        if (StringUtils.isBlank(config.getSplitFooter())) {
            addErrorToContext(ctx, getPath(SPLIT_FOOTER_PATH, index), "Split footer not set");
            return false;
        }
        return true;
    }

    private String getPath(String path, int index) {
        return String.format(path, index);
    }

    private void addErrorToContext(ConstraintValidatorContext context, String path,
                                   String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addNode(path)
                .addConstraintViolation();
    }
}
