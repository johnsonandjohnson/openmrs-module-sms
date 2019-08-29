package org.openmrs.module.sms.api.http;

import org.apache.commons.httpclient.Header;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatuses;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.util.SmsEventSubjects;

import java.util.Collections;
import java.util.List;

import static org.joda.time.DateTime.now;
import static org.openmrs.module.sms.api.audit.SmsDirection.OUTBOUND;
import static org.openmrs.module.sms.api.util.SmsEvents.outboundEvent;

/**
 * Deals with multi-line responses, like the ones sent by Clickatell.
 */
public class MultilineResponseHandler extends ResponseHandler {

    /**
     * Constructs an instance using the provided template and configuration.
     * @param template the template to use
     * @param config the configuration to use
     */
    MultilineResponseHandler(Template template, Config config) {
        super(template, config);
    }

    @Override
    public void handle(OutgoingSms sms, String response, Header[] headers) {

        //
        // as the class name suggest we're dealing with a provider which returns a status code for each individual
        // recipient phone number in the original request on a separate line, ie: if we send an SMS to 4 recipients
        // then we should receive four lines of provider_message_id & status information
        //
        for (String responseLine : response.split("\\r?\\n")) {

            String[] messageIdAndRecipient = getTemplateOutgoingResponse().extractSuccessMessageIdAndRecipient(
                    responseLine);

            if (messageIdAndRecipient == null) {
                Integer failureCount = sms.getFailureCount() + 1;
                String[] messageAndRecipient;

                messageAndRecipient = getTemplateOutgoingResponse().extractFailureMessageAndRecipient(responseLine);
                if (messageAndRecipient == null) {
                    getEvents().add(outboundEvent(getConfig().retryOrAbortSubject(failureCount), getConfig().getName(),
                            sms.getRecipients(), sms.getMessage(), sms.getMotechId(), null, failureCount, null, null, sms.getCustomParams()));

                    String errorMessage = String.format(
                            "Failed to send SMS. Template error. Can't parse response: %s", responseLine);
                    getLogger().error(errorMessage);
                    warn(errorMessage);

                    getAuditRecords().add(new SmsRecord(getConfig().getName(), OUTBOUND, sms.getRecipients().toString(),
                            sms.getMessage(), now(), getConfig().retryOrAbortStatus(failureCount), null,
                            sms.getMotechId(), null, null));
                } else {
                    String failureMessage = messageAndRecipient[0];
                    String recipient = messageAndRecipient[1];
                    List<String> recipients = Collections.singletonList(recipient);
                    getEvents().add(outboundEvent(getConfig().retryOrAbortSubject(failureCount), getConfig().getName(),
                            recipients, sms.getMessage(), sms.getMotechId(), null, failureCount, null, null, sms.getCustomParams()));
                    getLogger().info(String.format("Failed to send SMS: %s", failureMessage));
                    getAuditRecords().add(new SmsRecord(getConfig().getName(), OUTBOUND, recipient, sms.getMessage(),
                            now(), getConfig().retryOrAbortStatus(failureCount), null, sms.getMotechId(), null,
                            failureMessage));
                }
            } else {
                String messageId = messageIdAndRecipient[0];
                String recipient = messageIdAndRecipient[1];
                List<String> recipients = Collections.singletonList(recipient);
                //todo: HIPAA concerns?
                getLogger().info(String.format("Sent messageId %s '%s' to %s", messageId, messageForLog(sms),
                        recipient));
                getAuditRecords().add(new SmsRecord(getConfig().getName(), OUTBOUND, recipient, sms.getMessage(), now(),
                        DeliveryStatuses.DISPATCHED, null, sms.getMotechId(), messageId, null));
                getEvents().add(outboundEvent(SmsEventSubjects.DISPATCHED, getConfig().getName(), recipients,
                        sms.getMessage(), sms.getMotechId(), messageId, null, null, null, sms.getCustomParams()));
            }
        }
    }
}
