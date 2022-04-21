package org.openmrs.module.sms.api.web.dto;

import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.SmsRecords;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.module.sms.api.web.dto.builder.SmsRecordDTOBuilder;
import org.openmrs.module.sms.domain.PagingInfo;

import java.util.ArrayList;
import java.util.List;

public class SmsRecordsPageable extends BaseDTO {

    private static final long serialVersionUID = -2734653856427001890L;

    private Integer pageIndex;

    private Integer pageSize;

    private Integer totalRecords;

    private List<SmsRecordDTO> rows;

    public SmsRecordsPageable() {
    }

    public SmsRecordsPageable(PagingInfo page, SmsRecords smsRecords) {
        this.pageIndex = page.getPage();
        this.pageSize = page.getPageSize();
        this.totalRecords = smsRecords.getCount();
        this.rows = convertSmsRecordsToDTO(smsRecords.getRecords());
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<SmsRecordDTO> getRows() {
        return rows;
    }

    public void setRows(List<SmsRecordDTO> rows) {
        this.rows = rows;
    }

    private List<SmsRecordDTO> convertSmsRecordsToDTO(List<SmsRecord> records) {
        List<SmsRecordDTO> result = new ArrayList<>(records.size());
        for (SmsRecord smsRecord : records) {
            result.add(new SmsRecordDTOBuilder()
                    .setId(smsRecord.getId())
                    .setErrorMessage(smsRecord.getErrorMessage())
                    .setProviderStatus(smsRecord.getProviderStatus())
                    .setOpenMrsId(smsRecord.getOpenMrsId())
                    .setProviderId(smsRecord.getProviderId())
                    .setDeliveryStatus(smsRecord.getDeliveryStatus())
                    .setMessageContent(smsRecord.getMessageContent())
                    .setTimestamp(DateUtil.getDateWithLocalTimeZone(smsRecord.getTimestamp()))
                    .setConfig(smsRecord.getConfig())
                    .setSmsDirection(smsRecord.getSmsDirection())
                    .setPhoneNumber(smsRecord.getPhoneNumber())
                    .setModificationDate(DateUtil.getDateWithLocalTimeZone(smsRecord.getDateChanged()))
                    .setCreationDate(DateUtil.getDateWithLocalTimeZone(smsRecord.getDateCreated()))
                    .setModifiedBy(smsRecord.getChangedBy())
                    .setCreator(smsRecord.getCreator())
                    .createSmsRecordDTO());
        }
        return result;
    }
}
