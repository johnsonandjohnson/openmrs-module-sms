package org.openmrs.module.sms.api.configs;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatuses;

import static org.junit.Assert.assertEquals;

public class ConfigTest {

    private static final int FAILURE_COUNT = 3;
    private Config config;

    @Before
    public void setup() {
        config = new Config();
        config.setMaxRetries(FAILURE_COUNT);
    }

    @Test
    public void shouldReturnRetryThenAbortSubject() {
        assertEquals(DeliveryStatuses.RETRYING, config.retryOrAbortSubject(FAILURE_COUNT - 1));
        assertEquals(DeliveryStatuses.ABORTED, config.retryOrAbortSubject(FAILURE_COUNT));
    }

    @Test
    public void shouldReturnRetryThenAbortStatus() {
        assertEquals(DeliveryStatuses.RETRYING, config.retryOrAbortStatus(FAILURE_COUNT - 1));
        assertEquals(DeliveryStatuses.ABORTED, config.retryOrAbortStatus(FAILURE_COUNT));
    }
}
