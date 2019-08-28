package org.openmrs.module.sms.api.audit;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.motechproject.commons.api.MotechEnumUtils.toEnumSet;

/**
 * Service that does the reading and writing to the SMS audit log.
 */
@Service("smsAuditService")
public class SmsAuditServiceImpl implements SmsAuditService {

    @Autowired
    private SmsRecordDao smsRecordDao;

    @Override
    @Transactional
    public List<SmsRecord> findAllSmsRecords() {
        return smsRecordDao.retrieveAll();
    }

    @Override
    @Transactional
    public SmsRecords findAllSmsRecords(SmsRecordSearchCriteria criteria) {
        List<SmsRecord> recordList = smsRecordDao.findByCriteria(criteria.getConfig(),
                EnumSet.allOf(SmsDirection.class), criteria.getPhoneNumber(),
                criteria.getMessageContent(), criteria.getTimestampRange(), criteria.getDeliveryStatuses(),
                criteria.getProviderStatus(), criteria.getMotechId(), criteria.getProviderId(), criteria.getErrorMessage(),
                criteria.getOrder());

        return new SmsRecords(recordList.size(), recordList);
    }

    @Override
    @Transactional
    public long countAllSmsRecords(SmsRecordSearchCriteria criteria) {
        return smsRecordDao.countFindByCriteria(criteria.getConfig(),
                EnumSet.allOf(SmsDirection.class), criteria.getPhoneNumber(),
                criteria.getMessageContent(), criteria.getTimestampRange(), criteria.getDeliveryStatuses(),
                criteria.getProviderStatus(), criteria.getMotechId(), criteria.getProviderId(), criteria.getErrorMessage());
    }
}
