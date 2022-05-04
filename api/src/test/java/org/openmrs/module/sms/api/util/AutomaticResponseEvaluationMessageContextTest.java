package org.openmrs.module.sms.api.util;

import org.openmrs.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.handler.IncomingMessageData;
import org.openmrs.module.sms.api.handler.IncomingMessageDataBuilder;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

public class AutomaticResponseEvaluationMessageContextTest {

	private AutomaticResponseEvaluationMessageContext automaticResponseEvaluationMessageContext ;

	private final String CONFIG_NAME = "nexmo";

	private final String MESSAGE = "TEST_DATA";

	private final String SENDER = "22210212";

	private IncomingMessageData incomingMessageData;

	@Before
	public void setup()
	{
		Config config = new Config();
		config.setName(CONFIG_NAME);

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
	public void assertAutomaticResponseEvaluationMessageContext()
	{
		assertThat(incomingMessageData.getConfig(),is(automaticResponseEvaluationMessageContext.getConfig()));
		assertThat(incomingMessageData.getMessage(),is(automaticResponseEvaluationMessageContext.getMessage()));
		assertThat(incomingMessageData.getProviderMessageId(),is(automaticResponseEvaluationMessageContext.getProviderMessageId()));
		assertThat(incomingMessageData.getReceivedAt(),is(automaticResponseEvaluationMessageContext.getReceivedAt()));
		assertThat(incomingMessageData.getSenderPhoneNumber(),is(automaticResponseEvaluationMessageContext.getSenderPhoneNumber()));
		assertThat(incomingMessageData.getDeliveryStatus(),is(automaticResponseEvaluationMessageContext.getDeliveryStatus()));
		assertThat(incomingMessageData.getSenderPhoneNumber(), is(automaticResponseEvaluationMessageContext.getSenderPhoneNumber()));
	}
}
