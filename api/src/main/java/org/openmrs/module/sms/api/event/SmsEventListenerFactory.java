package org.openmrs.module.sms.api.event;

import org.openmrs.event.Event;

public final class SmsEventListenerFactory {

	public static void registerEventListeners() {
		subscribeListener(new SendSmsEventListener());
		subscribeListener(new StatusSmsEventListener());
	}

	private static void subscribeListener(AbstractSmsEventListener smsEventListener) {
		for (String subject : smsEventListener.getSubjects()) {
			Event.subscribe(subject, smsEventListener);
		}
	}

	private SmsEventListenerFactory() {
	}
}
