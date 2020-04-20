package org.openmrs.module.sms.api.service;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sms.api.util.PrivilegeConstants;

/**
 * Service that allows sending SMS messages.
 */
public interface SmsService extends OpenmrsService {

    /**
     * This method allows sending outgoing sms messages through HTTP. The configuration specified in the {@link OutgoingSms}
     * object will be used for dealing with the provider.
     *
     * @param message the representation of the sms to send
     */
    @Authorized(PrivilegeConstants.SMS_MODULE_PRIVILEGE)
    void send(final OutgoingSms message);
}
