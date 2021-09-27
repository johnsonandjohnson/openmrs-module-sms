package org.openmrs.module.sms.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.service.SmsService;
import org.openmrs.module.sms.api.util.SmsEventSubjects;

import java.util.Arrays;
import java.util.Map;

public class SendSmsEventListener extends AbstractSmsEventListener {

    private static final Log LOGGER = LogFactory.getLog(SendSmsEventListener.class);

    @Override
    public String[] getSubjects() {
        return new String[]{SmsEventSubjects.SEND_SMS};
    }

    @Override
    protected void handleEvent(Map<String, Object> properties) {
        LOGGER.info(String.format("Handling external event %s: %s", Arrays.toString(getSubjects()),
                properties.get("message").toString().replace("\n", "\\n")));
        getComponent("sms.SmsService", SmsService.class)
                .send(new OutgoingSms(new SmsEvent(properties)));
    }
}
