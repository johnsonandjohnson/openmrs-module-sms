package org.openmrs.module.sms.api.util;

import org.apache.commons.lang.NotImplementedException;

import javax.validation.ConstraintValidatorContext;

public class MockedViolationBuilder implements ConstraintValidatorContext.ConstraintViolationBuilder {
 
    @Override
    public NodeBuilderDefinedContext addNode(String s) {
        return new MockedNodeContextBuilder();
    }

    @Override
    public ConstraintValidatorContext addConstraintViolation() {
        throw new NotImplementedException();
    }


    class MockedNodeContextBuilder implements ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext {
        @Override
        public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addNode(String s) {
            throw new NotImplementedException();
        }

        @Override
        public ConstraintValidatorContext addConstraintViolation() {
            return null;
        }
    }
}
