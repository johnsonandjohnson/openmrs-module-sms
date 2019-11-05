package org.openmrs.module.sms.api.util;

import org.openmrs.module.sms.api.event.SmsEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * SmsEvent Helper class, builds events from provided parameters.
 */
public final class SmsEvents {

    /**
     * Creates an OpenMRS event which will describe an outbound SMS (already sent or to be sent).
     * @param subject the subject of the evnet
     * @param config the configuration which
     * @param recipients the recipient numbers for the SMS
     * @param message the SMS message
     * @param openMrsId the OpenMRS ID of the message
     * @param providerMessageId the provider ID of the message
     * @param failureCount the failure counter for this message, will be used for retries
     * @param providerStatus the SMS status coming from the provider
     * @param timestamp the timestamp of the message
     * @param customParams custom parameters for the provider
     * @return a {@link SmsEvent} describing the outbound SMS
     */
    public static SmsEvent outboundEvent(String subject, String config, //NO CHECKSTYLE ParameterNumber
                                            List<String> recipients, String message, String openMrsId,
                                            String providerMessageId, Integer failureCount, String providerStatus,
                                            Date timestamp, Map<String, String> customParams) {
        Map<String, Object> params = new HashMap<>();
        params.put(SmsEventParams.CONFIG, config);
        params.put(SmsEventParams.RECIPIENTS, recipients);
        params.put(SmsEventParams.MESSAGE, message);
        params.put(SmsEventParams.OPENMRS_ID, openMrsId);
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
            params.put(SmsEventParams.TIMESTAMP, DateUtil.now());
        } else {
            params.put(SmsEventParams.TIMESTAMP, timestamp);
        }
        if (customParams != null && !customParams.isEmpty()) {
            params.put(SmsEventParams.CUSTOM_PARAMS, customParams);
        }
        return new SmsEvent(subject, params);
    }

    private SmsEvents() { }
}
