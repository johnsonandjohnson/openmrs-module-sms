package org.openmrs.module.sms.api.audit;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.domain.PagingInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service that does the reading and writing to the SMS audit log.
 */
public class SmsAuditServiceImpl extends BaseOpenmrsService implements SmsAuditService {

    private SmsRecordDao smsRecordDao;

    public void setSmsRecordDao(SmsRecordDao smsRecordDao) {
        this.smsRecordDao = smsRecordDao;
    }

    @Override
    @Transactional
    public List<SmsRecord> findAllSmsRecords() {
        return smsRecordDao.retrieveAll();
    }

    @Override
    @Transactional
    public SmsRecords findAllSmsRecords(SmsRecordSearchCriteria criteria) {
        List<SmsRecord> recordList = smsRecordDao.findByCriteria(criteria);

        return new SmsRecords(recordList.size(), recordList);
    }

    @Override
    @Transactional
    public long countAllSmsRecords(SmsRecordSearchCriteria criteria) {
        return smsRecordDao.countFindByCriteria(criteria);
    }

    @Override
    public SmsRecords findPageableByCriteria(PagingInfo pagingInfo, SmsRecordSearchCriteria criteria) {
        List<SmsRecord> result = smsRecordDao.findPageableByCriteria(pagingInfo, criteria);
        int size = pagingInfo.shouldLoadRecordCount() ? result.size() : pagingInfo.getTotalRecordCount().intValue();
        return new SmsRecords(size, result);
    }
}
