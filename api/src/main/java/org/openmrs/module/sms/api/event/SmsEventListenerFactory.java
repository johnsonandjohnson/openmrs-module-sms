package org.openmrs.module.sms.api.event;

import org.openmrs.api.context.Context;
import org.openmrs.event.Event;

import java.util.List;

public final class SmsEventListenerFactory {

	public static void registerEventListeners() {
		List<AbstractSmsEventListener> eventComponents = Context.getRegisteredComponents(AbstractSmsEventListener.class);
		for (AbstractSmsEventListener eventListener : eventComponents) {
			subscribeListener(eventListener);
		}
	}

	public static void unRegisterEventListeners() {
		List<AbstractSmsEventListener> eventComponents = Context.getRegisteredComponents(AbstractSmsEventListener.class);
		for (AbstractSmsEventListener eventListener : eventComponents) {
			unSubscribeListener(eventListener);
		}
	}

	private static void subscribeListener(AbstractSmsEventListener smsEventListener) {
		for (String subject : smsEventListener.getSubjects()) {
			Event.subscribe(subject, smsEventListener);
		}
	}

	private static void unSubscribeListener(AbstractSmsEventListener smsEventListener) {
		for (String subject : smsEventListener.getSubjects()) {
			Event.unsubscribe(subject, smsEventListener);
		}
	}

	private SmsEventListenerFactory() {
	}
}
