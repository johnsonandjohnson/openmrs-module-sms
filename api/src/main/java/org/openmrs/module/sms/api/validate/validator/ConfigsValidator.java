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

@Component
public class ConfigsValidator implements ConstraintValidator<ValidConfigs, Configs> {

    private static final String NAME_PATH = "configs[%d].name";
    private static final String TEMPLATE_PATH = "configs[%d].template";
    private static final String MAX_RETRIES_PATH = "configs[%d].maxRetries";
    private static final String SPLIT_HEADER_PATH = "configs[%d].splitHeader";
    private static final String SPLIT_FOOTER_PATH = "configs[%d].splitFooter";

    private static final String ERROR_EMPTY_CONFIG_NAME = "Empty config name";
    private static final String ERROR_NAME_IS_DUPLICATED = "%s name is duplicated";
    private static final String ERROR_TEMPLATE_NAME_NOT_SET = "Template name not set";
    private static final String ERROR_MAX_RETRIES_NOT_SET = "Max retries not set";
    private static final String ERROR_MAX_RETRIES_NUMBER_CANNOT_BE_NEGATIVE = "Max retries number cannot be negative";
    private static final String ERROR_SPLIT_HEADER_NOT_SET = "Split header not set";
    private static final String ERROR_SPLIT_FOOTER_NOT_SET = "Split footer not set";

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
        if (StringUtils.isEmpty(validatedConfig.getName())) {
            addErrorToContext(ctx, getPath(NAME_PATH, index), ERROR_EMPTY_CONFIG_NAME);
            isValid = false;
        }
        for (Config config : configList) {
            if (validatedConfig != config
                    && StringUtils.equals(validatedConfig.getName(), config.getName())) {
                addErrorToContext(ctx, getPath(NAME_PATH, index), String.format(ERROR_NAME_IS_DUPLICATED,
                        validatedConfig.getName()));
                isValid = false;
            }
        }

        return isValid;
    }

    private boolean isTemplateValid(Config config, int index, ConstraintValidatorContext ctx) {
        if (StringUtils.isBlank(config.getTemplateName())) {
            addErrorToContext(ctx, getPath(TEMPLATE_PATH, index), ERROR_TEMPLATE_NAME_NOT_SET);
            return false;
        }
        return true;
    }

    private boolean isMaxRetriesValid(Config config, int index, ConstraintValidatorContext ctx) {
        if (config.getMaxRetries() == null) {
            addErrorToContext(ctx, getPath(MAX_RETRIES_PATH, index), ERROR_MAX_RETRIES_NOT_SET);
            return false;
        }

        if (config.getMaxRetries() < 0) {
            addErrorToContext(ctx, getPath(MAX_RETRIES_PATH, index), ERROR_MAX_RETRIES_NUMBER_CANNOT_BE_NEGATIVE);
            return false;
        }
        return true;
    }

    private boolean isSplitHeaderValid(Config config, int index, ConstraintValidatorContext ctx) {
        if (StringUtils.isBlank(config.getSplitHeader())) {
            addErrorToContext(ctx, getPath(SPLIT_HEADER_PATH, index), ERROR_SPLIT_HEADER_NOT_SET);
            return false;
        }
        return true;
    }

    private boolean isSplitFooterValid(Config config, int index, ConstraintValidatorContext ctx) {
        if (StringUtils.isBlank(config.getSplitFooter())) {
            addErrorToContext(ctx, getPath(SPLIT_FOOTER_PATH, index), ERROR_SPLIT_FOOTER_NOT_SET);
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
