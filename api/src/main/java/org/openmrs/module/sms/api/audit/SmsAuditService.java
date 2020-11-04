package org.openmrs.module.sms.api.audit;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sms.api.templates.Status;
import org.openmrs.module.sms.api.util.PrivilegeConstants;
import org.openmrs.module.sms.domain.PagingInfo;

import java.util.List;
import java.util.Map;

/**
 * Reading and writing to the SMS audit log
 */
public interface SmsAuditService extends OpenmrsService {

    /**
     * Finds and returns all <code>SmsRecord</code> entries in the sms log.
     *
     * @return all sms records in the sms log
     */
    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    List<SmsRecord> findAllSmsRecords();

    /**
     * Finds and returns all <code>SmsRecords</code> entries matching the specified
     * search criteria.
     *
     * @return all sms records matching the provided criteria
     */
    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    SmsRecords findAllSmsRecords(SmsRecordSearchCriteria criteria);

    /**
     * Returns the count of <code>SmsRecords</code> entries matching the specified
     * search criteria.
     *
     * @return the count of sms records matching the provided criteria
     */
    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    long countAllSmsRecords(SmsRecordSearchCriteria criteria);

    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    SmsRecords findPageableByCriteria(PagingInfo pagingInfo, SmsRecordSearchCriteria criteria);

    SmsRecord createOrUpdate(Status status, String configName, Map<String, String> params);
}
