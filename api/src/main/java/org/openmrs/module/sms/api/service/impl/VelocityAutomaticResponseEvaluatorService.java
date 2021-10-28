package org.openmrs.module.sms.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.VelocityException;
import org.openmrs.api.APIException;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.service.AutomaticResponseEvaluatorService;
import org.openmrs.module.sms.api.util.AutomaticResponseEvaluationMessageContext;
import org.openmrs.module.sms.api.util.VelocityContextFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

import static java.text.MessageFormat.format;

/**
 * The VelocityAutomaticResponseEvaluatorService Class.
 *
 * <p>The implementation of AutomaticResponseEvaluatorService which resolves {@link
 * Config#getAutomaticResponseScript()} as Apache Velocity Template which creates the text of the
 * automatic response.
 */
public class VelocityAutomaticResponseEvaluatorService
    implements AutomaticResponseEvaluatorService {

  @Override
  public Optional<String> evaluate(AutomaticResponseEvaluationMessageContext messageContext) {
    if (messageContext.getConfig().getAutomaticResponseScript() == null) {
      return Optional.empty();
    }

    final VelocityContext velocityContext = VelocityContextFactory.createDefaultContext();
    velocityContext.put("messageContext", messageContext);

    final StringWriter writer = new StringWriter();

    try {
      final boolean evaluationResult =
          Velocity.evaluate(
              velocityContext,
              writer,
              messageContext.getConfig().getName(),
              messageContext.getConfig().getAutomaticResponseScript());

      if (!evaluationResult) {
        throw new APIException(
            format(
                "Failed to execute Velocity Template for automatic response configured by: {0}, see logs for further "
                    + "information.",
                messageContext.getConfig().getName()));
      }
    } catch (IOException | VelocityException e) {
      throw new APIException(
          format(
              "Failed to execute Velocity Template Template for response configured " + "by: {0}.",
              messageContext.getConfig().getName()),
          e);
    }

    final String evaluationResult = writer.toString();
    return StringUtils.isBlank(evaluationResult) ? Optional.empty() : Optional.of(evaluationResult);
  }
}
