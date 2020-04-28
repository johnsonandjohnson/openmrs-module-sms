package org.openmrs.module.sms.api.validate.annotation;

import org.openmrs.module.sms.api.validate.validator.ConfigsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Constraint(validatedBy = {ConfigsValidator.class} )
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidConfigs {

    /**
     * Specify the message in case of a validation error
     *
     * @return the message about the error
     */
    String message() default "invalid SMS config";

    /**
     * Specify validation groups, to which this constraint belongs
     *
     * @return array with group classes
     */
    Class<?>[] groups() default {
    };

    /**
     * Specify custom payload objects
     *
     * @return array with payload classes
     */
    Class<? extends Payload>[] payload() default {
    };
}
