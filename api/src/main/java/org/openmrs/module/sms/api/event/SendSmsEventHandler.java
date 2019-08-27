package org.openmrs.module.sms.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.openmrs.module.sms.api.http.SmsHttpService;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.service.SmsService;
import org.openmrs.module.sms.api.util.SmsEventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * When another module sends an SMS, it calls SmsService.send, which in turn sends one or more SEND_SMS events which
 * are handled here and passed straight through to to SmsHttpService.send
 */
@Service
public class SendSmsEventHandler {

    private static final Log LOGGER = LogFactory.getLog(SendSmsEventHandler.class);

    private SmsHttpService smsHttpService;
    private SmsService smsService;

    @Autowired
    public SendSmsEventHandler(SmsHttpService smsHttpService, SmsService smsService) {
        this.smsHttpService = smsHttpService;
        this.smsService = smsService;
    }

    @MotechListener (subjects = { SmsEventSubjects.SEND_SMS })
    public void handleExternal(MotechEvent event) {
        LOGGER.info(String.format("Handling external event %s: %s", event.getSubject(),
                event.getParameters().get("message").toString().replace("\n", "\\n")));
        smsService.send(new OutgoingSms(event));
    }

    @MotechListener (subjects = { SmsEventSubjects.PENDING, SmsEventSubjects.SCHEDULED, SmsEventSubjects.RETRYING })
    public void handleInternal(MotechEvent event) {
        LOGGER.info(String.format("Handling internal event %s: %s", event.getSubject(),
                event.getParameters().get("message").toString().replace("\n", "\\n")));
        smsHttpService.send(new OutgoingSms(event));
    }
}

