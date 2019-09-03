package org.openmrs.module.sms.api.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.http.SmsHttpService;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.scheduler.tasks.AbstractTask;

public class SmsScheduledTask extends AbstractTask {

	@Override
	public void execute() {
		Context.getRegisteredComponent("sms.SmsHttpService", SmsHttpService.class)
				.send(new OutgoingSms(new SmsEvent(SmsEvent.convertProperties(getTaskDefinition().getProperties()))));
	}
}
