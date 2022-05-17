package org.openmrs.module.sms.api.service;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.data.AutomaticResponseData;
import org.openmrs.module.sms.api.handler.IncomingMessageData;
import org.openmrs.module.sms.api.handler.IncomingMessageDataBuilder;
import org.openmrs.module.sms.api.service.impl.VelocityAutomaticResponseEvaluatorService;
import org.openmrs.module.sms.api.util.AutomaticResponseEvaluationMessageContext;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Optional;

import static org.junit.Assert.assertFalse;

public class VelocityAutomaticResponseEvaluatorServiceTest extends BaseModuleContextSensitiveTest {

	private IncomingMessageData incomingMessageData;

	private final String CONFIG_NAME = "nexmo";

	private final String MESSAGE = "TEST_DATA";

	private final String SENDER = "22210212";

	private AutomaticResponseEvaluationMessageContext automaticResponseEvaluationMessageContext;

	@Before
	public void setup() {
		Config config = new Config();
		config.setName(CONFIG_NAME);
		config.setAutomaticResponseScript("");
		incomingMessageData = new IncomingMessageDataBuilder()
				.setConfig(config)
				.setMessage(MESSAGE)
				.setProviderMessageId("TEST_ID")
				.setReceivedAt(DateUtil.now())
				.setReceivedForAFistTime(Boolean.TRUE)
				.setDeliveryStatus(DeliveryStatusesConstants.SCHEDULED)
				.setSender(SENDER)
				.build();
		automaticResponseEvaluationMessageContext = new AutomaticResponseEvaluationMessageContext(incomingMessageData);
	}

	@Test
	public void shouldReturnEmpty(){
		VelocityAutomaticResponseEvaluatorService velocityAutomaticResponseEvaluatorService = new VelocityAutomaticResponseEvaluatorService();
		Optional<AutomaticResponseData> automaticResponseData = velocityAutomaticResponseEvaluatorService.evaluate(automaticResponseEvaluationMessageContext);
		assertFalse(automaticResponseData.isPresent());
	}
}
