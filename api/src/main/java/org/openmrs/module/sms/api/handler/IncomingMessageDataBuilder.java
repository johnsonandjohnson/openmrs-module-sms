package org.openmrs.module.sms.api.handler;

import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.util.IncomingMessageAccessor;

import java.util.Date;

/** The IncomingMessageDataBuilder Class, a builder for {@link IncomingMessageData}. */
public final class IncomingMessageDataBuilder {
  private boolean receivedForAFistTime = true;
  private Config config;
  private String sender;
  private String message;
  private Date receivedAt;
  private String deliveryStatus;
  private String providerMessageId;

  public IncomingMessageDataBuilder() {}

  public IncomingMessageDataBuilder(IncomingMessageAccessor messageAccessor) {
    sender = messageAccessor.getSender();
    message = messageAccessor.getMessage();
    deliveryStatus = messageAccessor.getStatus();
    providerMessageId = messageAccessor.getMsgId();
  }

  public IncomingMessageData build() {
    return new IncomingMessageData(receivedForAFistTime, config, sender, message, receivedAt, deliveryStatus, providerMessageId);
  }

  public IncomingMessageDataBuilder setReceivedForAFistTime(boolean receivedForAFistTime) {
    this.receivedForAFistTime = receivedForAFistTime;
    return this;
  }

  public IncomingMessageDataBuilder setConfig(Config config) {
    this.config = config;
    return this;
  }

  public IncomingMessageDataBuilder setSender(String sender) {
    this.sender = sender;
    return this;
  }

  public IncomingMessageDataBuilder setMessage(String message) {
    this.message = message;
    return this;
  }

  public IncomingMessageDataBuilder setReceivedAt(Date receivedAt) {
    this.receivedAt = receivedAt;
    return this;
  }

  public IncomingMessageDataBuilder setDeliveryStatus(String deliveryStatus) {
    this.deliveryStatus = deliveryStatus;
    return this;
  }

  public IncomingMessageDataBuilder setProviderMessageId(String providerMessageId) {
    this.providerMessageId = providerMessageId;
    return this;
  }
}
