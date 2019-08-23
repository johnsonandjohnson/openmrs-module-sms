package org.openmrs.module.sms.api.dao;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;
import org.openmrs.api.db.OpenmrsDataDAO;
import org.openmrs.module.sms.api.audit.SmsDirection;
import org.openmrs.module.sms.api.audit.SmsRecord;

import java.util.List;
import java.util.Set;

import static org.motechproject.mds.util.Constants.Operators.MATCHES;
import static org.motechproject.mds.util.Constants.Operators.MATCHES_CASE_INSENSITIVE;

/**
 * Used to query and save audit records in the database.
 * This is a service interface for which MDS will generate the implementation at runtime.
 */
public interface SmsRecordDao extends OpenmrsDataDAO<SmsRecord> {

    /**
     * Retrieves all sms records matching the given criteria. All string fields in this lookup are matched
     * using the matches() operator, meaning they will be matched using the same rules that {@link String#matches(String)}
     * uses.
     * @param config the name of the configuration associated with the SMS message
     * @param directions the set of directions (inbound, outbound)
     * @param phoneNumber the number of the phone the message was received from or delivered to
     * @param messageContent the contents of the SMS message
     * @param timestamp the date-time range the timestamp of the SMS should fall into
     * @param deliveryStatuses the set of delivery status for the messages
     * @param providerStatus
     * @param motechId the id by which MOTECH identifies the message
     * @param providerId the provider generated ID for the SMS
     * @param errorMessage the error message for the SMS
     * @return the matching records
     */
    List<SmsRecord> findByCriteria(String config, Set<SmsDirection> directions, String phoneNumber, String messageContent, Range<DateTime> timestamp,
                                   Set<String> deliveryStatuses, String providerStatus, String motechId, String providerId, String errorMessage);

    /**
     * Retrieves the total count of SMS messages matching the given criteria. All string fields in this lookup are matched
     * using the matches() operator, meaning they will be matched using the same rules that {@link String#matches(String)}
     * uses.
     * @param config the name of the configuration associated with the SMS message
     * @param directions the set of directions (inbound, outbound)
     * @param phoneNumber the number of the phone the message was received from or delivered to
     * @param messageContent the contents of the SMS message
     * @param timestamp the date-time range the timestamp of the SMS should fall into
     * @param deliveryStatuses the set of delivery status for the messages
     * @param providerStatus
     * @param motechId the id by which MOTECH identifies the message
     * @param providerId the provider generated ID for the SMS
     * @param errorMessage the error message for the SMS
     * @return the matching records
     */
    long countFindByCriteria(String config, Set<SmsDirection> directions, String phoneNumber, String messageContent, Range<DateTime> timestamp,
                             Set<String> deliveryStatuses, String providerStatus, String motechId, String providerId, String errorMessage);

    /**
     * Retrieves records by the provider ID.
     * @param providerId the provider ID
     * @return the list of matching records
     */
    List<SmsRecord> findByProviderId(String providerId);

    /**
     * Retrieves records by the ID.
     * @param motechId the MOTECH ID
     * @return the list of matching records
     */
    List<SmsRecord> findByMotechId(String motechId);

    /**
     * Retrieves records by both provider and MOTECH IDs.
     * @param providerId the provider ID
     * @param motechId the MOTECH ID
     * @return the list of matching records
     */
    List<SmsRecord> findByProviderAndMotechId(String providerId, String motechId);

    void deleteAll();

    SmsRecord create(SmsRecord smsRecord);

    List<SmsRecord> retrieveAll();
}
