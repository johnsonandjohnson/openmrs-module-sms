package org.openmrs.module.sms.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.http.SmsHttpService;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.util.SmsEventSubjectsConstants;

import java.util.Arrays;
import java.util.Map;

public class StatusSmsEventListener extends AbstractSmsEventListener {

    private static final Log LOGGER = LogFactory.getLog(StatusSmsEventListener.class);

    @Override
    public String[] getSubjects() {
        return new String[]{SmsEventSubjectsConstants.PENDING, SmsEventSubjectsConstants.SCHEDULED, SmsEventSubjectsConstants.RETRYING};
    }

    @Override
    protected void handleEvent(Map<String, Object> properties) {
        LOGGER.info(String.format("Handling external event %s: %s", Arrays.toString(getSubjects()),
                properties.get("message").toString().replace("\n", "\\n")));
        getComponent("sms.SmsHttpService", SmsHttpService.class)
                .send(new OutgoingSms(new SmsEvent(properties)));
    }
}
