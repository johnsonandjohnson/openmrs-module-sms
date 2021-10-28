package org.openmrs.module.sms.api.handler.impl;

import org.openmrs.module.sms.api.handler.IncomingMessageData;
import org.openmrs.module.sms.api.handler.IncomingMessageHandler;
import org.openmrs.module.sms.api.service.AutomaticResponseEvaluatorService;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.service.SmsService;
import org.openmrs.module.sms.api.util.AutomaticResponseEvaluationMessageContext;

import java.util.Optional;

/**
 * The default implementation of {@link IncomingMessageHandler}.
 *
 * <p>The handler sends a message to the incoming message's sender. The message is created by an
 * {@link AutomaticResponseEvaluatorService}.
 */
public class DefaultIncomingMessageHandler implements IncomingMessageHandler {
  private AutomaticResponseEvaluatorService automaticResponseEvaluatorService;
  private SmsService smsService;

  @Override
  public int priority() {
    return IncomingMessageHandler.BUILD_IN_PRIORITY;
  }

  @Override
  public boolean handle(IncomingMessageData message) {
    // Don't process repeated messages
    if (!message.isReceivedForAFistTime()) {
      return false;
    }

    final Optional<String> automaticResponse =
        automaticResponseEvaluatorService.evaluate(
            new AutomaticResponseEvaluationMessageContext(message));

    if (automaticResponse.isPresent()) {
      final OutgoingSms automaticResponseSms =
          new OutgoingSms(
              message.getConfig().getName(), message.getSender(), automaticResponse.get());
      smsService.send(automaticResponseSms);
    }

    return false;
  }

  public void setAutomaticResponseEvaluatorService(
      AutomaticResponseEvaluatorService automaticResponseEvaluatorService) {
    this.automaticResponseEvaluatorService = automaticResponseEvaluatorService;
  }

  public void setSmsService(SmsService smsService) {
    this.smsService = smsService;
  }
}
