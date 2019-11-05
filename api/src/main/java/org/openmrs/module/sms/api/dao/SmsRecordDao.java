package org.openmrs.module.sms.api.dao;

import org.openmrs.api.db.OpenmrsDataDAO;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.SmsRecordSearchCriteria;
import org.openmrs.module.sms.domain.PagingInfo;

import java.util.List;

/**
 * Used to query and save audit records in the database.
 * This is a service interface for which MDS will generate the implementation at runtime.
 */
public interface SmsRecordDao extends OpenmrsDataDAO<SmsRecord> {

    /**
     * Retrieves all sms records matching the given criteria. All string fields in this lookup are matched
     * using the matches() operator, meaning they will be matched using the same rules that {@link String#matches(String)}
     * uses.
     * @param searchCriteria the search criteria
     * @return the matching records
     */
    List<SmsRecord> findByCriteria(SmsRecordSearchCriteria searchCriteria);

    /**
     * Retrieves the total count of SMS messages matching the given criteria. All string fields in this lookup are matched
     * using the matches() operator, meaning they will be matched using the same rules that {@link String#matches(String)}
     * uses.
     * @param searchCriteria the search criteria
     * @return the matching records
     */
    long countFindByCriteria(SmsRecordSearchCriteria searchCriteria);

    /**
     * Retrieves records by the provider ID.
     * @param providerId the provider ID
     * @return the list of matching records
     */
    List<SmsRecord> findByProviderId(String providerId);

    /**
     * Retrieves records by the ID.
     * @param openMrsId the OpenMRS ID
     * @return the list of matching records
     */
    List<SmsRecord> findByOpenMrsId(String openMrsId);

    /**
     * Retrieves records by both provider and OpenMRS IDs.
     * @param providerId the provider ID
     * @param openMrsId the OpenMRS ID
     * @return the list of matching records
     */
    List<SmsRecord> findByProviderAndOpenMrsId(String providerId, String openMrsId);

    void deleteAll();

    SmsRecord create(SmsRecord smsRecord);

    List<SmsRecord> retrieveAll();

    /**
     * Retrieves records by the specified criteria and based on the specified paging object.
     * @param pagingInfo  The {@link PagingInfo} object contains pageable configuration.
     * @param searchCriteria The {@link SmsRecordSearchCriteria} object specifying condition for criteria
     * @return The list of matching records
     */
    List<SmsRecord> findPageableByCriteria(PagingInfo pagingInfo, SmsRecordSearchCriteria searchCriteria);
}
