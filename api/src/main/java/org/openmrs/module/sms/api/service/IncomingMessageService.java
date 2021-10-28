package org.openmrs.module.sms.api.service;

import org.openmrs.module.DaemonToken;
import org.openmrs.module.sms.api.handler.IncomingMessageData;

/**
 * The IncomingMessageService Interface.
 *
 * <p>The IncomingMessage service is responsible for facilitating of incoming message processing in
 * the system.
 */
public interface IncomingMessageService {
  /**
   * Runs processing for an incoming message.
   *
   * <p>Execute processing for all configured {@link
   * org.openmrs.module.sms.api.handler.IncomingMessageHandler}s. Any errors coming from handlers
   * are handled by this method.
   *
   * @param incomingMessage the message to handle, not null
   * @implNote Error in one handler, doesn't stop following handlers from executing.
   */
  void handleMessageSafe(IncomingMessageData incomingMessage);

  /**
   * Sets Daemon Token for this instance.
   *
   * @param token the token, not null
   */
  void setDaemonToken(DaemonToken token);
}
