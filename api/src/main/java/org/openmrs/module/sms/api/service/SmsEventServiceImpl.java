package org.openmrs.module.sms.api.service;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.event.Event;
import org.openmrs.event.EventMessage;
import org.openmrs.module.sms.api.event.SmsEvent;

import java.io.Serializable;
import java.util.Map;

public class SmsEventServiceImpl extends BaseOpenmrsService implements SmsEventService {

	@Override
	public void sendEventMessage(SmsEvent event) {
		Event.fireEvent(event.getSubject(), convertParamsToEventMessage(event.getParameters()));
	}

	private EventMessage convertParamsToEventMessage(Map<String, Object> params) {
		EventMessage eventMessage = new EventMessage();

		for(String key : params.keySet()) {
			eventMessage.put(key, (Serializable) params.get(key));
		}

		return eventMessage;
	}
}
