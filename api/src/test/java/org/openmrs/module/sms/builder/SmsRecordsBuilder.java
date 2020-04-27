package org.openmrs.module.sms.builder;

import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.SmsRecords;

import java.util.ArrayList;
import java.util.List;

public class SmsRecordsBuilder extends AbstractBuilder<SmsRecords> {

    private static final String OTHER_MESSAGE_2 = "other message 2";
    private static final String OTHER_MESSAGE_3 = "other message 3";
    private static final String OTHER_MESSAGE_4 = "other message 4";
    private static final String OTHER_MESSAGE_5 = "other message 5";
    private static final String OTHER_MESSAGE_6 = "other message 6";
    private int count;
    private List<SmsRecord> records;

    public SmsRecordsBuilder() {
        this.records = buildRecords();
        this.count = this.records.size();
    }

    @Override
    public SmsRecords build() {
        SmsRecords smsRecords = new SmsRecords();
        smsRecords.setCount(this.count);
        smsRecords.setRecords(this.records);
        return smsRecords;
    }

    @Override
    public SmsRecords buildAsNew() {
        return build();
    }

    private List<SmsRecord> buildRecords() {
        List<SmsRecord> result = new ArrayList<>();
        result.add(new SmsRecordBuilder().build());
        result.add(new SmsRecordBuilder()
                .withProviderId(OTHER_MESSAGE_2)
                .build());
        result.add(new SmsRecordBuilder()
                .withProviderId(OTHER_MESSAGE_3)
                .build());
        result.add(new SmsRecordBuilder()
                .withProviderId(OTHER_MESSAGE_4)
                .build());
        result.add(new SmsRecordBuilder()
                .withProviderId(OTHER_MESSAGE_5)
                .build());
        result.add(new SmsRecordBuilder()
                .withProviderId(OTHER_MESSAGE_6)
                .build());
        return result;
    }
}
