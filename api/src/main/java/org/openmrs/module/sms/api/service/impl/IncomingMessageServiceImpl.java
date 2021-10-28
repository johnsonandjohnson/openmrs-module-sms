package org.openmrs.module.sms.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.sms.api.handler.IncomingMessageData;
import org.openmrs.module.sms.api.handler.IncomingMessageHandler;
import org.openmrs.module.sms.api.service.IncomingMessageService;
import org.openmrs.module.sms.api.util.Constants;

import java.util.Iterator;
import java.util.List;

import static java.util.Comparator.comparing;

/** The default implementation of IncomingMessageService. */
public class IncomingMessageServiceImpl implements IncomingMessageService {
  private static final Log LOGGER = LogFactory.getLog(IncomingMessageService.class);

  private DaemonToken daemonToken;

  @Override
  public void handleMessageSafe(IncomingMessageData incomingMessage) {
    if (tokenNotSet(incomingMessage) || handlersDisabled()) {
      return;
    }

    try {
      Daemon.runInDaemonThreadAndWait(() -> internalHandleMessage(incomingMessage), daemonToken);
    } catch (Exception e) {
      LOGGER.error(
          "Failed to handle incoming message, message providerId: "
              + incomingMessage.getProviderMessageId());
    }
  }

  @Override
  public void setDaemonToken(DaemonToken token) {
    this.daemonToken = token;
  }

  private boolean tokenNotSet(IncomingMessageData incomingMessage) {
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

  private boolean handlersDisabled() {
    final String parameterValue =
        Context.getAdministrationService()
            .getGlobalProperty(Constants.GP_DISABLE_INCOMING_MESSAGE_HANDLERS);
    return Boolean.parseBoolean(parameterValue);
  }

  private void internalHandleMessage(IncomingMessageData incomingMessage) {
    final Iterator<IncomingMessageHandler> handlersChain = getHandlers();

    while (handlersChain.hasNext()) {
      final IncomingMessageHandler currentHandler = handlersChain.next();
      handle(incomingMessage, currentHandler);
    }
  }

  private Iterator<IncomingMessageHandler> getHandlers() {
    final List<IncomingMessageHandler> allHandlers =
        Context.getRegisteredComponents(IncomingMessageHandler.class);
    allHandlers.sort(comparing(IncomingMessageHandler::priority).reversed());
    return allHandlers.iterator();
  }

  private void handle(IncomingMessageData incomingMessage, IncomingMessageHandler handler) {
    try {
      handler.handle(incomingMessage);
    } catch (Exception e) {
      LOGGER.error("Error executing Incoming Message handler: " + handler.getClass().getName(), e);
    }
  }
}
