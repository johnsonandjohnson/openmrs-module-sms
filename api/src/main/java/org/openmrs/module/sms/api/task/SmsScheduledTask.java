package org.openmrs.module.sms.api.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.http.SmsHttpService;
import org.openmrs.module.sms.api.service.OutgoingSms;

public class SmsScheduledTask extends AbstractSmsTask {

	@Override
	public void executeTask() {
		Context.getRegisteredComponent("sms.SmsHttpService", SmsHttpService.class)
				.send(new OutgoingSms(new SmsEvent(SmsEvent.convertProperties(getTaskDefinition().getProperties()))));
	}
}
