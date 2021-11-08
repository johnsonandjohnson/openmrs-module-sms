package org.openmrs.module.sms.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.sms.api.handler.IncomingMessageData;
import org.openmrs.module.sms.api.service.IncomingMessageService;

/**
 * The base class for IncomingMessageService implementations, provides implementation of common
 * methods.
 */
public abstract class BaseIncomingMessageService implements IncomingMessageService {
  protected static final Log LOGGER = LogFactory.getLog(IncomingMessageService.class);

  private DaemonToken daemonToken;

  @Override
  public void setDaemonToken(DaemonToken token) {
    this.daemonToken = token;
  }

  protected DaemonToken getDaemonToken() {
    return daemonToken;
  }

  protected boolean isTokenNotSet(IncomingMessageData incomingMessage) {
    if (daemonToken == null) {
      LOGGER.warn(
          "Received message before IncomingMessageService Service received Daemon Token (most likely before SMS "
              + "module startup). The incoming message is not handled (e.g.: automatic response won't be sent)! "
              + "Provider ID of the received message: "
              + incomingMessage.getProviderMessageId());
      return true;
    }

    return false;
  }
}
