package org.openmrs.module.sms.api.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.motechproject.commons.date.util.DateUtil.now;


/**
 * MotechEvent Helper class, builds events from provided parameters.
 */
public final class SmsEvents {

    /**
     * Creates an MOTECH event which will describe an outbound SMS (already sent or to be sent).
     * @param subject the subject of the evnet
     * @param config the configuration which
     * @param recipients the recipient numbers for the SMS
     * @param message the SMS message
     * @param motechId the MOTECH ID of the message
     * @param providerMessageId the provider ID of the message
     * @param failureCount the failure counter for this message, will be used for retries
     * @param providerStatus the SMS status coming from the provider
     * @param timestamp the timestamp of the message
     * @param customParams custom parameters for the provider
     * @return a {@link MotechEvent} describing the outbound SMS
     */
    public static MotechEvent outboundEvent(String subject, String config, //NO CHECKSTYLE ParameterNumber
                                            List<String> recipients, String message, String motechId,
                                            String providerMessageId, Integer failureCount, String providerStatus,
                                            DateTime timestamp, Map<String, String> customParams) {
        Map<String, Object> params = new HashMap<>();
        params.put(SmsEventParams.CONFIG, config);
        params.put(SmsEventParams.RECIPIENTS, recipients);
        params.put(SmsEventParams.MESSAGE, message);
        params.put(SmsEventParams.MOTECH_ID, motechId);
        if (providerMessageId != null) {
            params.put(SmsEventParams.PROVIDER_MESSAGE_ID, providerMessageId);
        }
        if (failureCount != null) {
            params.put(SmsEventParams.FAILURE_COUNT, failureCount);
        } else {
            params.put(SmsEventParams.FAILURE_COUNT, 0);
        }
        if (providerStatus != null) {
            params.put(SmsEventParams.PROVIDER_STATUS, providerStatus);
        }
        if (timestamp == null) {
            params.put(SmsEventParams.TIMESTAMP, now());
        } else {
            params.put(SmsEventParams.TIMESTAMP, timestamp);
        }
        if (customParams != null && !customParams.isEmpty()) {
            params.put(SmsEventParams.CUSTOM_PARAMS, customParams);
        }
        return new MotechEvent(subject, params);
    }

    private SmsEvents() { }
}
