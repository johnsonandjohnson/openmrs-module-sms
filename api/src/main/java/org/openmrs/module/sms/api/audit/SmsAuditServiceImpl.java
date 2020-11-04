package org.openmrs.module.sms.api.audit;

import org.hibernate.criterion.Order;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.api.templates.Status;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.module.sms.domain.PagingInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * Service that does the reading and writing to the SMS audit log.
 */
public class SmsAuditServiceImpl extends BaseOpenmrsService implements SmsAuditService {

    @Override
    @Transactional
    public List<SmsRecord> findAllSmsRecords() {
        return getSmsRecordDao().retrieveAll();
    }

    @Override
    @Transactional
    public SmsRecords findAllSmsRecords(SmsRecordSearchCriteria criteria) {
        List<SmsRecord> recordList = getSmsRecordDao().findByCriteria(criteria);

        return new SmsRecords(recordList.size(), recordList);
    }

    @Override
    @Transactional
    public long countAllSmsRecords(SmsRecordSearchCriteria criteria) {
        return getSmsRecordDao().countFindByCriteria(criteria);
    }

    @Override
    public SmsRecords findPageableByCriteria(PagingInfo pagingInfo, SmsRecordSearchCriteria criteria) {
        List<SmsRecord> result = getSmsRecordDao().findPageableByCriteria(pagingInfo, criteria);
        int size = pagingInfo.shouldLoadRecordCount() ? result.size() : pagingInfo.getTotalRecordCount().intValue();
        return new SmsRecords(size, result);
    }

    @Override
    @Transactional
    public SmsRecord createOrUpdate(Status status, String configName, Map<String, String> params) {
        String statusString = params.get(status.getStatusKey());
        String providerMessageId = params.get(status.getMessageIdKey());
        SmsRecords smsRecords = findMatchingSmsRecords(configName, providerMessageId);
        SmsRecord existingSmsRecord = findFirstByProviderMessageId(smsRecords, providerMessageId);

        SmsRecord smsRecord = getSmsRecord(existingSmsRecord, configName, statusString, providerMessageId);
        return getSmsRecordDao().createOrUpdate(smsRecord);
    }

    private SmsRecord getSmsRecord(SmsRecord existingSmsRecord, String configName, String statusString,
                                   String providerMessageId) {
        SmsRecord smsRecord;
        if (existingSmsRecord != null) {
            smsRecord = existingSmsRecord;
            smsRecord.setProviderStatus(statusString);
        } else {
            smsRecord = new SmsRecord(configName, SmsDirection.OUTBOUND, null, null, DateUtil.now(),
                    null, statusString, null, providerMessageId, null);
        }
        return smsRecord;
    }

    private SmsRecords findMatchingSmsRecords(String configName, String providerMessageId) {
        Order order = Order.desc("timestamp");
        return findAllSmsRecords(new SmsRecordSearchCriteria()
                .withConfig(configName)
                .withProviderId(providerMessageId)
                .withOrder(order));
    }

    private SmsRecord findFirstByProviderMessageId(SmsRecords smsRecords, String providerMessageId) {
        for (SmsRecord smsRecord : smsRecords.getRecords()) {
            if (smsRecord.getProviderId().equals(providerMessageId)) {
                return smsRecord;
            }
        }
        return null;
    }

    private SmsRecordDao getSmsRecordDao() {
        return Context.getRegisteredComponent("sms.SmsRecordDao", SmsRecordDao.class);
    }
}
