package org.openmrs.module.sms.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sms.api.event.SmsEvent;

public interface SmsEventService extends OpenmrsService {

	void sendEventMessage(SmsEvent event);
}
