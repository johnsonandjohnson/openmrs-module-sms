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
 * Deals with providers who return a generic response in the body or header
 */
public class GenericResponseHandler extends ResponseHandler {

    /**
     * Constructs an instance using the provided template and configuration.
     *
     * @param template the template to use
     * @param config   the configuration to use
     */
    GenericResponseHandler(Template template, Config config) {
        super(template, config);
    }

    @Override
    public void handle(OutgoingSms sms, String response, Header[] headers) {

        String providerStatus = getTemplateOutgoingResponse().extractProviderStatus(response);

        if (!getTemplateOutgoingResponse().hasSuccessResponse() ||
                getTemplateOutgoingResponse().checkSuccessResponse(response)) {

            String providerMessageId = extractProviderMessageId(headers, response);

            //todo: HIPAA concerns?
            getLogger().info(String.format("Sent messageId %s '%s' to %s", providerMessageId, messageForLog(sms),
                    sms.getRecipients().toString()));
            for (String recipient : sms.getRecipients()) {
                getAuditRecords().add(new SmsRecord(getConfig().getName(), OUTBOUND, recipient, sms.getMessage(), DateUtil.now(),
                        DeliveryStatuses.DISPATCHED, providerStatus, sms.getOpenMrsId(), providerMessageId, null));
            }
        } else {
            Integer failureCount = sms.getFailureCount() + 1;

            String failureMessage = getTemplateOutgoingResponse().extractSingleFailureMessage(response);
            if (failureMessage == null) {
                failureMessage = response;
            }
            getEvents().add(outboundEvent(getConfig().retryOrAbortSubject(failureCount), getConfig().getName(),
                    sms.getRecipients(), sms.getMessage(), sms.getOpenMrsId(), null, failureCount, null, null, sms.getCustomParams()));
            getLogger().debug(String.format("Failed to send SMS: %s", failureMessage));
            for (String recipient : sms.getRecipients()) {
                getAuditRecords().add(new SmsRecord(getConfig().getName(), OUTBOUND, recipient, sms.getMessage(), DateUtil.now(),
                        getConfig().retryOrAbortStatus(failureCount), providerStatus, sms.getOpenMrsId(), null, failureMessage));
            }
        }
    }

    private String extractProviderMessageId(Header[] headers, String response) {
        String providerMessageId = null;

        if (getTemplateOutgoingResponse().hasHeaderMessageId()) {
            for (Header header : headers) {
                if (header.getName().equals(getTemplateOutgoingResponse().getHeaderMessageId())) {
                    providerMessageId = header.getValue();
                }
            }
            if (providerMessageId == null) {
                String message = String.format("Unable to find provider message id in '%s' header",
                        getTemplateOutgoingResponse().getHeaderMessageId());
                getLogger().error(message);
            }
        } else if (getTemplateOutgoingResponse().hasSingleSuccessMessageId()) {
            providerMessageId = getTemplateOutgoingResponse().extractSingleSuccessMessageId(response);
        }

        return providerMessageId;
    }
}
