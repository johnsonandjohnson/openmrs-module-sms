package org.openmrs.module.sms.api.audit;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.query.QueryParams;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                toEnumSet(SmsDirection.class, criteria.getSmsDirections()), criteria.getPhoneNumber(),
                criteria.getMessageContent(), criteria.getTimestampRange(), criteria.getDeliveryStatuses(),
                criteria.getProviderStatus(), criteria.getMotechId(), criteria.getProviderId(), criteria.getErrorMessage());

        return new SmsRecords(recordList.size(), recordList);
    }

    @Override
    @Transactional
    public long countAllSmsRecords(SmsRecordSearchCriteria criteria) {
        return smsRecordDao.countFindByCriteria(criteria.getConfig(),
                toEnumSet(SmsDirection.class, criteria.getSmsDirections()), criteria.getPhoneNumber(),
                criteria.getMessageContent(), criteria.getTimestampRange(), criteria.getDeliveryStatuses(),
                criteria.getProviderStatus(), criteria.getMotechId(), criteria.getProviderId(), criteria.getErrorMessage());
    }
/*
    private Object executeQuery(SmsRecordSearchCriteria criteria, boolean count) {
        Set<String> directions = criteria.getSmsDirections();
        Set<SmsDirection> directionsEnum = toEnumSet(SmsDirection.class, directions);
        Set<String> statuses = criteria.getDeliveryStatuses();
        Range<DateTime> timestampRange = criteria.getTimestampRange();
        String config = asQuery(criteria.getConfig());
        String phoneNumber = asQuery(criteria.getPhoneNumber());
        String messageContent = asQuery(criteria.getMessageContent());
        String providerStatus = asQuery(criteria.getProviderStatus());
        String motechId = asQuery(criteria.getMotechId());
        String providerId = asQuery(criteria.getProviderId());
        String errorMessage = asQuery(criteria.getErrorMessage());

        if (count) {
            return smsRecordDao.countFindByCriteria(config, directionsEnum, phoneNumber, messageContent,
                    timestampRange, statuses, providerStatus, motechId, providerId, errorMessage);
        } else {
            return smsRecordDao.findByCriteria(
                    config, directionsEnum, phoneNumber, messageContent, timestampRange, statuses,
                    providerStatus, motechId, providerId, errorMessage);
        }
    }
*/
    private String asQuery(String value) {
        return StringUtils.isNotBlank(value) ? String.format(".*%s.*", value) : value;
    }

}
