package org.openmrs.module.sms.api.web.dto;

import org.junit.Test;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.SmsRecords;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.module.sms.builder.SmsRecordsBuilder;
import org.openmrs.module.sms.domain.PagingInfo;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SmsRecordsPageableTest {

    public static final int PAGE = 1;
    public static final int PAGE_SIZE = 231;

    @Test
    public void convertSmsRecordsToDTO() {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPage(PAGE);
        pagingInfo.setPageSize(PAGE_SIZE);
        SmsRecords smsRecords = new SmsRecordsBuilder().build();
        SmsRecordsPageable smsRecordsPageable = new SmsRecordsPageable(pagingInfo, smsRecords);
        assertThat(smsRecordsPageable.getPageIndex(), is(PAGE));
        assertThat(smsRecordsPageable.getPageSize(), is(PAGE_SIZE));
        assertThat(smsRecordsPageable.getTotalRecords(), is(smsRecords.getCount()));
        assertDTOs(smsRecordsPageable.getRows(), smsRecords.getRecords());
    }

    @Test
    public void shouldWorkWithEmptyRecords() {
        SmsRecordsPageable smsRecordsPageable = new SmsRecordsPageable();
        smsRecordsPageable.setPageIndex(PAGE);
        smsRecordsPageable.setPageSize(PAGE_SIZE);
        smsRecordsPageable.setRows(new ArrayList<>());
        smsRecordsPageable.setTotalRecords(0);
        assertThat(smsRecordsPageable.getPageIndex(), is(PAGE));
        assertThat(smsRecordsPageable.getPageSize(), is(PAGE_SIZE));
        assertThat(smsRecordsPageable.getTotalRecords(), is(0));
        assertThat(smsRecordsPageable.getRows(), is(empty()));
    }

    private void assertDTOs(List<SmsRecordDTO> rows, List<SmsRecord> records) {
        for (int i = 0; i < records.size(); i++) {
            SmsRecordDTO actual = rows.get(i);
            SmsRecordDTO expected = toDTO(records.get(i));
            assertThat(actual.getId(), is(expected.getId()));
            assertThat(actual.getErrorMessage(), is(expected.getErrorMessage()));
            assertThat(actual.getProviderStatus(), is(expected.getProviderStatus()));
            assertThat(actual.getOpenMrsId(), is(expected.getOpenMrsId()));
            assertThat(actual.getProviderId(), is(expected.getProviderId()));
            assertThat(actual.getDeliveryStatus(), is(expected.getDeliveryStatus()));
            assertThat(actual.getMessageContent(), is(expected.getMessageContent()));
            assertThat(actual.getTimestamp(), is(expected.getTimestamp()));
            assertThat(actual.getConfig(), is(expected.getConfig()));
            assertThat(actual.getSmsDirection(), is(expected.getSmsDirection()));
            assertThat(actual.getPhoneNumber(), is(expected.getPhoneNumber()));
        }
    }

    private SmsRecordDTO toDTO(SmsRecord smsRecord) {
        SmsRecordDTO dto = new SmsRecordDTO();
        dto.setId(smsRecord.getId());
        dto.setErrorMessage(smsRecord.getErrorMessage());
        dto.setProviderStatus(smsRecord.getProviderStatus());
        dto.setOpenMrsId(smsRecord.getOpenMrsId());
        dto.setProviderId(smsRecord.getProviderId());
        dto.setDeliveryStatus(smsRecord.getDeliveryStatus());
        dto.setMessageContent(smsRecord.getMessageContent());
        dto.setTimestamp(DateUtil.dateToString(smsRecord.getTimestamp()));
        dto.setConfig(smsRecord.getConfig());
        dto.setSmsDirection(smsRecord.getSmsDirection().name());
        dto.setPhoneNumber(smsRecord.getPhoneNumber());
        return dto;
    }
}