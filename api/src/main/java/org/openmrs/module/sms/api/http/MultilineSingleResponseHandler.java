package org.openmrs.module.sms.api.http;

import org.apache.commons.httpclient.Header;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatuses;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.util.DateUtil;

import static org.openmrs.module.sms.api.audit.SmsDirection.OUTBOUND;
import static org.openmrs.module.sms.api.util.SmsEvents.outboundEvent;

/**
 * Deals with providers who return multi-line responses, but return a different response when sending only one message,
 * like Clickatell does.
 */
public class MultilineSingleResponseHandler extends ResponseHandler {

    /**
     * Constructs an instance using the provided template and configuration.
     * @param template the template to use
     * @param config the configuration to use
     */
    MultilineSingleResponseHandler(Template template, Config config) {
        super(template, config);
    }

    @Override
    public void handle(OutgoingSms sms, String response, Header[] headers) {

        String messageId = getTemplateOutgoingResponse().extractSingleSuccessMessageId(response);

        if (messageId == null) {
            Integer failureCount = sms.getFailureCount() + 1;

            String failureMessage = getTemplateOutgoingResponse().extractSingleFailureMessage(response);
            if (failureMessage == null) {
                failureMessage = response;
            }
            getEvents().add(outboundEvent(getConfig().retryOrAbortSubject(failureCount), getConfig().getName(),
                    sms.getRecipients(), sms.getMessage(), sms.getMotechId(), null, failureCount, null, null, sms.getCustomParams()));
            getLogger().info(String.format("Failed to send SMS: %s", failureMessage));
            getAuditRecords().add(new SmsRecord(getConfig().getName(), OUTBOUND, sms.getRecipients().get(0),
                    sms.getMessage(), DateUtil.now(), getConfig().retryOrAbortStatus(failureCount), null, sms.getMotechId(),
                    null, failureMessage));
        } else {
            //todo: HIPAA concerns?
            getLogger().info(String.format("Sent messageId %s '%s' to %s", messageId, messageForLog(sms),
                    sms.getRecipients().get(0)));
            getAuditRecords().add(new SmsRecord(getConfig().getName(), OUTBOUND, sms.getRecipients().get(0),
                    sms.getMessage(), DateUtil.now(), DeliveryStatuses.DISPATCHED, null, sms.getMotechId(), messageId, null));
        }
    }
}
