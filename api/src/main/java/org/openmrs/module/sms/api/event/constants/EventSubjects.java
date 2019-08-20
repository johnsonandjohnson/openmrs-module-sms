package org.openmrs.module.sms.api.event.constants;

/**
 * Utility class for storing event subjects.
 */
public final class EventSubjects {

    private static final String BASE_SUBJECT = "org.openmrs.module.sms.api.api.";
    public static final String CONFIGS_CHANGED = BASE_SUBJECT + "configsChanged";

    private EventSubjects() {

    }
}
