package org.openmrs.module.sms.api.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.sms.api.http.SmsHttpService;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.HashMap;
import java.util.Map;

public class SmsScheduledTask extends AbstractTask {

	@Override
	public void execute() {
		Map<String, Object> properties = new HashMap<>();
		properties.putAll(getTaskDefinition().getProperties());

		Context.getRegisteredComponent("sms.SmsHttpService", SmsHttpService.class)
				.send(new OutgoingSms(properties));
	}
}
