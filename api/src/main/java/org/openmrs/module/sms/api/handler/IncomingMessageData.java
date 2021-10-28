package org.openmrs.module.sms.api.handler;

import org.openmrs.module.sms.api.configs.Config;

import java.util.Date;

/**
 * The immutable IncomingMessageData Class stores information about single message received by the
 * system.
 *
 * @see IncomingMessageDataBuilder
 */
public final class IncomingMessageData {
  private final boolean receivedForAFistTime;
  private final Config config;
  private final String sender;
  private final String message;
  private final Date receivedAt;
  private final String deliveryStatus;
  private final String providerMessageId;

  IncomingMessageData(
      boolean receivedForAFistTime,
      Config config,
      String sender,
      String message,
      Date receivedAt,
      String deliveryStatus,
      String providerMessageId) {
    this.receivedForAFistTime = receivedForAFistTime;
    this.config = config;
    this.sender = sender;
    this.message = message;
    this.receivedAt = receivedAt;
    this.deliveryStatus = deliveryStatus;
    this.providerMessageId = providerMessageId;
  }

  public boolean isReceivedForAFistTime() {
    return receivedForAFistTime;
  }

  public Config getConfig() {
    return config;
  }

  public String getSender() {
    return sender;
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
