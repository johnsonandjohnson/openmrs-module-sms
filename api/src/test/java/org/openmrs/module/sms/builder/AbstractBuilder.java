package org.openmrs.module.sms.builder;

public abstract class AbstractBuilder<T extends Object> {

    private static int instanceNumber;

    public abstract T build();

    public abstract T buildAsNew();

    protected AbstractBuilder() {
        instanceNumber++;
    }

    protected int getInstanceNumber() {
        return instanceNumber;
    }
}
