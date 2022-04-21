package org.openmrs.module.sms.api.util;

import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.handler.IncomingMessageData;

import java.util.Date;

/**
 * The AutomaticResponseEvaluationContext Class.
 * <p>The POJO with information related to the message which caused the Automatic Response.
 */
public class AutomaticResponseEvaluationMessageContext {
	private final Config config;
	private final String senderPhoneNumber;
	private final String message;
	private final Date receivedAt;
	private final String deliveryStatus;
	private final String providerMessageId;

	public AutomaticResponseEvaluationMessageContext(IncomingMessageData incomingMessage) {
		this.config = incomingMessage.getConfig();
		this.senderPhoneNumber = incomingMessage.getSenderPhoneNumber();
		this.message = incomingMessage.getMessage();
		this.receivedAt = incomingMessage.getReceivedAt();
		this.deliveryStatus = incomingMessage.getDeliveryStatus();
		this.providerMessageId = incomingMessage.getProviderMessageId();
	}

	public Config getConfig() {
		return config;
	}

	public String getSenderPhoneNumber() {
		return senderPhoneNumber;
	}

	public String getMessage() {
		return message;
	}

	public Date getReceivedAt() {
		return receivedAt;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public String getProviderMessageId() {
		return providerMessageId;
	}
}
