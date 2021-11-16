package org.openmrs.module.sms.api.service;

import org.openmrs.module.sms.api.data.AutomaticResponseData;
import org.openmrs.module.sms.api.util.AutomaticResponseEvaluationMessageContext;

import java.util.Optional;

/**
 * The AutomaticResponseEvaluatorService Class.
 *
 * <p>This service is responsible for evaluation what automatic message should be send back to a
 * sender of a message received by system.
 */
public interface AutomaticResponseEvaluatorService {
  Optional<AutomaticResponseData> evaluate(AutomaticResponseEvaluationMessageContext messageContext);
}
