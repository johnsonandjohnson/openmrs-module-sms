package org.openmrs.module.sms.api.web;

import org.junit.Test;
import org.openmrs.module.sms.api.audit.SmsRecordSearchCriteria;
import org.openmrs.module.sms.builder.GridSettingsBuilder;
import org.openmrs.module.sms.builder.SmsRecordSearchCriteriaBuilder;
import org.openmrs.module.sms.domain.PagingInfo;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GridSettingsTest {

    public static final int ROWS = 23;
    public static final int PAGE = 2;
    public static final String EXPECTED_REPRESENTATION = "GridSettings{rows=23, page=2, sortColumn='timestamp', " +
            "sortDirection='DESC', config='ConfigName', phoneNumber='1235123531223', " +
            "messageContent='Message content', timeFrom='2010-11-16 15:42:00', " +
            "timeTo='2020-04-23 11:16:32', deliveryStatus='DISPATCHED,ABORTED', " +
            "providerStatus='ProviderStatus', smsDirection='OUTBOUND,INBOUND', " +
            "openMrsId='null', providerId='null'}";

    @Test
    public void createCriteriaAsExpected() {
        GridSettings settings = new GridSettingsBuilder().build();
        SmsRecordSearchCriteria expectedSearchCriteria = new SmsRecordSearchCriteriaBuilder().build();
        Interval expectedInterval = expectedSearchCriteria.getTimestampRange();
        SmsRecordSearchCriteria actual = settings.toSmsRecordSearchCriteria();
        assertThat(actual.getTimestampRange(), is(expectedInterval));
        assertThat(actual.getTimestampRange().getFrom(), is(expectedInterval.getFrom()));
        assertThat(actual.getTimestampRange().getTo(), is(expectedInterval.getTo()));
        assertThat(actual.getConfig(), is(expectedSearchCriteria.getConfig()));
        assertThat(actual.getSmsDirections(), is(expectedSearchCriteria.getSmsDirections()));
        assertThat(actual.getDeliveryStatuses(), is(expectedSearchCriteria.getDeliveryStatuses()));
        assertThat(actual.getProviderStatus(), is(expectedSearchCriteria.getProviderStatus()));
        assertThat(actual.getOrder().toString(), is(expectedSearchCriteria.getOrder().toString()));
        assertThat(actual.getPhoneNumber(), is(expectedSearchCriteria.getPhoneNumber()));
        assertThat(actual.getMessageContent(), is(expectedSearchCriteria.getMessageContent()));
        assertThat(actual.getErrorMessage(), is(expectedSearchCriteria.getErrorMessage()));
        assertThat(actual.toString(), is(expectedSearchCriteria.toString()));
    }

    @Test
    public void toPageInfo() {
        GridSettings settings = new GridSettingsBuilder().build();
        PagingInfo expected = new PagingInfo(PAGE, ROWS);
        PagingInfo actual = settings.toPageInfo();
        assertThat(actual.getPage(), is(expected.getPage()));
        assertThat(actual.getPageSize(), is(expected.getPageSize()));
        assertThat(actual.getTotalRecordCount(), is(expected.getTotalRecordCount()));
        assertThat(actual.shouldLoadRecordCount(), is(expected.shouldLoadRecordCount()));
        assertThat(actual, is(expected));
    }

    @Test
    public void toStringShouldReturnAllImportantInformation() {
        GridSettings settings = new GridSettingsBuilder().build();
        assertThat(settings.toString(), is(EXPECTED_REPRESENTATION));
    }
}