/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.sms.api.http;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sms.ContextSensitiveWithActivatorTest;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.SmsRecordSearchCriteria;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.openmrs.module.sms.api.configs.Configs;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.api.service.ConfigService;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.service.SmsEventService;
import org.openmrs.module.sms.api.service.TemplateService;
import org.openmrs.module.sms.builder.ConfigsBuilder;
import org.openmrs.module.sms.builder.OutgoingSmsBuilder;
import org.openmrs.module.sms.builder.SmsRecordBuilder;
import org.openmrs.module.sms.mock.SmsHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SmsHttpServiceTest extends ContextSensitiveWithActivatorTest {

	private static final String ERROR_WITHOUT_MESSAGE_ID =
			"{\n      \"to\": \"447700900000\",\n      "
					+ "\"status\": \"0\",\n      \"remaining-balance\": \"3.14159265\",\n      \"message-price\": "
					+ "\"0.03330000\",\n      \"network\": \"12345\",\n      \"client-ref\": \"my-personal-reference\",\n "
					+ "     \"account-ref\": \"customer1234\"\n          }";
	private static final String NEXMO_GENERIC = "nexmo-generic";
	private static final String BAD_CREDENTIALS = "Bad Credentials";

	@Autowired
	@Qualifier("templateService")
	private TemplateService templateService;

	@Autowired
	@Qualifier("sms.configService")
	private ConfigService configService;

	@Autowired
	@Qualifier("sms.SmsRecordDao")
	private SmsRecordDao smsRecordDao;

	@Autowired
	@Qualifier("sms.eventService")
	private SmsEventService smsEventService;

	@Before
	public void setUp() {
		smsRecordDao.deleteAll();
		Configs configs = new ConfigsBuilder().buildAsNew();
		configService.updateConfigs(configs);
		templateService.loadTemplates();
	}

	@After
	public void cleanDatabase() throws Exception {
		smsRecordDao.deleteAll();
		this.deleteAllData();
	}

	@Test(expected = IllegalStateException.class)
	public void sendProcessWithInvalidAuthenticateParams() {
		final SmsHttpService smsHttpService = prepareSuccessAnswer(true);
		OutgoingSms sms = new OutgoingSmsBuilder().withWithAuthenticationParams(false).build();
		smsHttpService.send(sms);
	}

	@Test
	public void sendProcessWithSuccessMessage() {
		String message = UUID.randomUUID().toString();
		final SmsHttpService smsHttpService = prepareSuccessAnswer(true);
		OutgoingSms sms = new OutgoingSmsBuilder().withMessage(message).build();
		smsHttpService.send(sms);
		SmsRecord expected =
				new SmsRecordBuilder().withMessageContent(message).withOpenMrsId(null).build();
		SmsRecord actual =
				smsRecordDao
						.findByCriteria(new SmsRecordSearchCriteria().withMessageContent(message))
						.get(0);
		assertSmsRecord(expected, actual);
	}

	@Test
	public void sendProcessWithSuccessStatusWithoutMessageId() {
		final SmsHttpService smsHttpService = prepareSuccessAnswer(false);
		OutgoingSms sms = new OutgoingSmsBuilder().build();
		smsHttpService.send(sms);
		SmsRecord expected =
				new SmsRecordBuilder()
						.withOpenMrsId(null)
						.withProviderId(null)
						.withErrorMessage(ERROR_WITHOUT_MESSAGE_ID)
						.withDeliveryStatus(DeliveryStatusesConstants.ABORTED)
						.build();
		SmsRecord actual = smsRecordDao.getAll(true).get(0);
		assertSmsRecord(expected, actual);
	}

	@Test
	public void sendProcessWithGeneralFailureMessage() {
		final SmsHttpService smsHttpService = prepareFailureAnswer();
		OutgoingSms sms = new OutgoingSmsBuilder().build();
		smsHttpService.send(sms);
		SmsRecord expected =
				new SmsRecordBuilder()
						.withOpenMrsId(null)
						.withProviderId(null)
						.withProviderStatus(null)
						.withErrorMessage(BAD_CREDENTIALS)
						.withDeliveryStatus(DeliveryStatusesConstants.ABORTED)
						.build();
		SmsRecord actual = smsRecordDao.getAll(true).get(0);
		assertSmsRecord(expected, actual);
	}

	@Test
	public void sendSuccessMessageWithGenericHandler() {
		String message = UUID.randomUUID().toString();
		final SmsHttpService smsHttpService = prepareSuccessAnswer(true);
		OutgoingSms sms =
				new OutgoingSmsBuilder().withConfig(NEXMO_GENERIC).withMessage(message).build();
		smsHttpService.send(sms);
		SmsRecord expected =
				new SmsRecordBuilder()
						.withConfig(NEXMO_GENERIC)
						.withMessageContent(message)
						.withProviderStatus(null)
						.withOpenMrsId(null)
						.build();
		SmsRecord actual =
				smsRecordDao
						.findByCriteria(new SmsRecordSearchCriteria().withMessageContent(message))
						.get(0);
		assertSmsRecord(expected, actual);
	}

	private SmsHttpService prepareSuccessAnswer(boolean withMessageId) {
		final SmsHttpService smsHttpService =
				new SmsHttpService(new SmsHttpClient().withMessageId(withMessageId));
		smsHttpService.setTemplateService(templateService);
		smsHttpService.setSmsRecordDao(smsRecordDao);
		smsHttpService.setSmsEventService(smsEventService);
		smsHttpService.setConfigService(configService);
		return smsHttpService;
	}

	private SmsHttpService prepareFailureAnswer() {
		final SmsHttpService smsHttpService =
				new SmsHttpService(new SmsHttpClient().withSuccessProcessed(false));
		smsHttpService.setTemplateService(templateService);
		smsHttpService.setSmsRecordDao(smsRecordDao);
		smsHttpService.setSmsEventService(smsEventService);
		smsHttpService.setConfigService(configService);
		return smsHttpService;
	}

	private void assertSmsRecord(SmsRecord expected, SmsRecord actual) {
		assertThat(actual.getConfig(), is(expected.getConfig()));
		assertThat(actual.getSmsDirection(), is(expected.getSmsDirection()));
		assertThat(actual.getPhoneNumber(), is(expected.getPhoneNumber()));
		assertThat(actual.getMessageContent(), is(expected.getMessageContent()));
		assertThat(actual.getDeliveryStatus(), is(expected.getDeliveryStatus()));
		assertThat(actual.getProviderStatus(), is(expected.getProviderStatus()));
		assertThat(actual.getOpenMrsId(), is(expected.getOpenMrsId()));
		assertThat(actual.getProviderId(), is(expected.getProviderId()));
		assertThat(actual.getErrorMessage(), is(expected.getErrorMessage()));
	}
}
