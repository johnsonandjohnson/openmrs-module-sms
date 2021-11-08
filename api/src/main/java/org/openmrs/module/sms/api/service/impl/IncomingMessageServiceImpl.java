package org.openmrs.module.sms.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.sms.api.handler.IncomingMessageData;
import org.openmrs.module.sms.api.handler.IncomingMessageHandler;
import org.openmrs.module.sms.api.util.Constants;

import java.util.Iterator;
import java.util.List;

import static java.util.Comparator.comparing;

/** The default implementation of IncomingMessageService. */
public class IncomingMessageServiceImpl extends BaseIncomingMessageService {
  @Override
  public void handleMessageSafe(IncomingMessageData incomingMessage) {
    if (isTokenNotSet(incomingMessage) || isHandlersDisabled()) {
      return;
    }

    try {
      Daemon.runInDaemonThreadAndWait(() -> internalHandleMessage(incomingMessage), getDaemonToken());
    } catch (Exception e) {
      LOGGER.error(
          "Failed to handle incoming message, message providerId: "
              + incomingMessage.getProviderMessageId());
    }
  }

  private boolean isHandlersDisabled() {
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
