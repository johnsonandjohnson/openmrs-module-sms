package org.openmrs.module.sms.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.sms.http.SmsHttpService;
import org.motechproject.sms.service.OutgoingSms;
import org.motechproject.sms.service.SmsService;
import org.motechproject.sms.util.SmsEventSubjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * When another module sends an SMS, it calls SmsService.send, which in turn sends one or more SEND_SMS events which
 * are handled here and passed straight through to to SmsHttpService.send
 */
@Service
public class SendSmsEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendSmsEventHandler.class);

    private SmsHttpService smsHttpService;
    private SmsService smsService;

    @Autowired
    public SendSmsEventHandler(SmsHttpService smsHttpService, SmsService smsService) {
        this.smsHttpService = smsHttpService;
        this.smsService = smsService;
    }

    @MotechListener (subjects = { SmsEventSubjects.SEND_SMS })
    public void handleExternal(MotechEvent event) {
        LOGGER.info("Handling external event {}: {}", event.getSubject(),
                event.getParameters().get("message").toString().replace("\n", "\\n"));
        smsService.send(new OutgoingSms(event));
    }

    @MotechListener (subjects = { SmsEventSubjects.PENDING, SmsEventSubjects.SCHEDULED, SmsEventSubjects.RETRYING })
    public void handleInternal(MotechEvent event) {
        LOGGER.info("Handling internal event {}: {}", event.getSubject(),
                event.getParameters().get("message").toString().replace("\n", "\\n"));
        smsHttpService.send(new OutgoingSms(event));
    }
}

