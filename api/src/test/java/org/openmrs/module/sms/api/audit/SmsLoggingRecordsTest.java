package org.openmrs.module.sms.api.audit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.module.sms.builder.SmsRecordsBuilder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DateUtil.class})
public class SmsLoggingRecordsTest {

    private static final int EXPECTED_PAGE = 1;
    private static final int EXPECTED_ROWS = 52;
    private static final long EXPECTED_TOTAL_RECORDS = 225;
    private static final String EXPECTED_DATE = "2010-11-16 15:40";

    @Before
    public void setUp() throws Exception {
        mockStatic(DateUtil.class);
        doReturn(EXPECTED_DATE).when(DateUtil.class, "getDateWithLocalTimeZone", anyObject());
    }

    @Test
    public void shouldMapSmsRecordsAsExpected() {
        SmsRecords expected = new SmsRecordsBuilder().build();
        SmsLoggingRecords actual = new SmsLoggingRecords(EXPECTED_PAGE, EXPECTED_ROWS,
                EXPECTED_TOTAL_RECORDS, expected);
        assertThat(actual.getRows().size(), is(expected.getRecords().size()));
        assertThat(actual.getPage(), is(EXPECTED_PAGE));
        assertThat(actual.getRecords(), is(EXPECTED_TOTAL_RECORDS));
        assertSmsRecords(actual.getRows().get(0), expected.getRecords().get(0));
    }

    private void assertSmsRecords(SmsLoggingRecord actual, SmsRecord expected) {
        assertThat(actual.getConfig(), is(expected.getConfig()));
        assertThat(actual.getPhoneNumber(), is(expected.getPhoneNumber()));
        assertThat(actual.getMessageContent(), is(expected.getMessageContent()));
        assertThat(actual.getTimestamp(), is(EXPECTED_DATE));
        assertThat(actual.getDeliveryStatus(), is(expected.getDeliveryStatus()));
        assertThat(actual.getProviderStatus(), is(expected.getProviderStatus()));
        assertThat(actual.getOpenMrsId(), is(expected.getOpenMrsId()));
        assertThat(actual.getProviderId(), is(expected.getProviderId()));
    }
}
