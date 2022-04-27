package org.openmrs.module.sms.api.service;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatusesConstants;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.configs.Configs;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.module.sms.builder.ConfigsBuilder;
import org.openmrs.module.sms.builder.OutgoingSmsBuilder;
import org.openmrs.module.sms.builder.SmsRecordBuilder;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SmsServiceImplTest extends BaseModuleContextSensitiveTest {

	public static final String LONG_TEXT = "Really long text which will cause exception if will be enough long. " +
			"Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
			"Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
			"when an unknown printer took a galley of type and scrambled it to make a type " +
			"specimen book. It has survived not only five centuries, but also the leap into " +
			"electronic typesetting, remaining essentially unchanged. It was popularised in " +
			"the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, " +
			"and more recently with desktop publishing software like Aldus " +
			"PageMaker including versions of Lorem Ipsum.";
	public static final String EXPECTED_PART_OF_MESSAGE = "Msg 1 of 5\nReally long text which will cause " +
			"exception if will be enough long. Lorem Ipsum is simply dummy text of the " +
			"printing and typesetting industry. \n...";

	@Autowired
	@Qualifier("sms.SmsService")
	private SmsService smsService;

	@Autowired
	@Qualifier("templateService")
	private TemplateService templateService;

	@Autowired
	@Qualifier("sms.configService")
	private ConfigService configService;

	@Autowired
	@Qualifier("sms.SmsRecordDao")
	private SmsRecordDao smsRecordDao;

	public SmsServiceImplTest() {
	}

	@Before
	public void setUp() {
		Configs configs = new ConfigsBuilder().buildAsNew();
		configService.updateConfigs(configs);
		templateService.loadTemplates();
	}

	@Test(expected = IllegalStateException.class)
	public void sendShouldThrowWhenMissingConfigs() {
		configService.updateConfigs(new Configs());
		OutgoingSms outgoingSms = new OutgoingSmsBuilder().build();
		smsService.send(outgoingSms);
	}

	@Test(expected = IllegalArgumentException.class)
	public void sendShouldThrowWhenSplitHeaderTooLong() {
		OutgoingSms outgoingSms = new OutgoingSmsBuilder().build();
		Config config = configService.getConfig(outgoingSms.getConfig());
		config.setSplitHeader(LONG_TEXT);
		smsService.send(outgoingSms);
	}

	@Test(expected = IllegalArgumentException.class)
	public void sendShouldThrowWhenSplitFooterTooLong() {
		OutgoingSms outgoingSms = new OutgoingSmsBuilder().build();
		Config config = configService.getConfig(outgoingSms.getConfig());
		config.setSplitFooter(LONG_TEXT);
		smsService.send(outgoingSms);
	}

	@Test
	public void sendWithShortMessage() {
		SmsRecord expected = new SmsRecordBuilder()
				.withOpenMrsId(null)
				.withDeliveryStatus(DeliveryStatusesConstants.PENDING)
				.withProviderStatus(null)
				.withProviderId(null)
				.build();
		OutgoingSms outgoingSms = new OutgoingSmsBuilder().build();
		smsService.send(outgoingSms);
		SmsRecord actual = smsRecordDao.getAll(true).get(0);
		assertSmsRecord(expected, actual);
	}

	@Test
	public void sendWithLongMessageAndMultipleRecipients() {
		SmsRecord expected = new SmsRecordBuilder()
				.withOpenMrsId(null)
				.withDeliveryStatus(DeliveryStatusesConstants.PENDING)
				.withProviderStatus(null)
				.withProviderId(null)
				.withMessageContent(EXPECTED_PART_OF_MESSAGE)
				.withPhoneNumber("213561315")
				.build();
		OutgoingSms outgoingSms = new OutgoingSmsBuilder()
				.withRecipients(Arrays.asList("213561315", "212566178"))
				.withMessage(LONG_TEXT)
				.build();
		smsService.send(outgoingSms);
		SmsRecord actual = smsRecordDao.getAll(true).get(0);
		assertSmsRecord(expected, actual);
	}

	@Test
	public void sendWithShortMessageAndDeliveryTime() {
		SmsRecord expected = new SmsRecordBuilder()
				.withOpenMrsId(null)
				.withDeliveryStatus(DeliveryStatusesConstants.SCHEDULED)
				.withProviderStatus(null)
				.withProviderId(null)
				.build();
		OutgoingSms outgoingSms = new OutgoingSmsBuilder()
				.withDeliveryTime(DateUtil.now())
				.build();
		smsService.send(outgoingSms);
		SmsRecord actual = smsRecordDao.getAll(true).get(0);
		assertSmsRecord(expected, actual);
	}

	@Test
	public void sendWithLongMessageDeliveryTimeAndRecipient() {
		SmsRecord expected = new SmsRecordBuilder()
				.withOpenMrsId(null)
				.withDeliveryStatus(DeliveryStatusesConstants.SCHEDULED)
				.withProviderStatus(null)
				.withProviderId(null)
				.withMessageContent(EXPECTED_PART_OF_MESSAGE)
				.withPhoneNumber("213561315")
				.build();
		OutgoingSms outgoingSms = new OutgoingSms("213561315",LONG_TEXT,DateUtil.now());
		smsService.send(outgoingSms);
		SmsRecord actual = smsRecordDao.getAll(true).get(0);
		assertSmsRecord(expected, actual);
	}

	@Test
	public void sendWithLongMessageAndRecipient() {
		SmsRecord expected = new SmsRecordBuilder()
				.withOpenMrsId(null)
				.withDeliveryStatus(DeliveryStatusesConstants.PENDING)
				.withProviderStatus(null)
				.withProviderId(null)
				.withMessageContent(EXPECTED_PART_OF_MESSAGE)
				.withPhoneNumber("213561315")
				.build();
		OutgoingSms outgoingSms = new OutgoingSms("213561315",LONG_TEXT);
		smsService.send(outgoingSms);
		SmsRecord actual = smsRecordDao.getAll(true).get(0);
		assertSmsRecord(expected, actual);
	}

	@Test
	public void sendWithLongMessageConfigurationAndRecipient() {
		SmsRecord expected = new SmsRecordBuilder()
				.withOpenMrsId(null)
				.withDeliveryStatus(DeliveryStatusesConstants.PENDING)
				.withProviderStatus(null)
				.withProviderId(null)
				.withConfig("nexmo")
				.withMessageContent(EXPECTED_PART_OF_MESSAGE)
				.withPhoneNumber("213561315")
				.build();
		OutgoingSms outgoingSms = new OutgoingSms("nexmo","213561315",LONG_TEXT);
		smsService.send(outgoingSms);
		SmsRecord actual = smsRecordDao.getAll(true).get(0);
		assertSmsRecord(expected, actual);
	}

	@Test
	public void sendWithLongMessageConfigurationDeliveryTimeAndRecipient() {
		SmsRecord expected = new SmsRecordBuilder()
				.withOpenMrsId(null)
				.withDeliveryStatus(DeliveryStatusesConstants.SCHEDULED)
				.withProviderStatus(null)
				.withProviderId(null)
				.withConfig("nexmo")
				.withMessageContent(EXPECTED_PART_OF_MESSAGE)
				.withPhoneNumber("213561315")
				.build();
		OutgoingSms outgoingSms = new OutgoingSms("nexmo","213561315",LONG_TEXT, DateUtil.now());
		smsService.send(outgoingSms);
		SmsRecord actual = smsRecordDao.getAll(true).get(0);
		assertSmsRecord(expected, actual);
	}

	private void assertSmsRecord(SmsRecord expected, SmsRecord actual) {
		assertThat(actual.getConfig(), is(expected.getConfig()));
		assertThat(actual.getSmsDirection(), is(expected.getSmsDirection()));
		assertThat(actual.getPhoneNumber(), is(expected.getPhoneNumber()));
		assertThat(actual.getMessageContent(), is(expected.getMessageContent()));
		assertThat(actual.getDeliveryStatus(), is(expected.getDeliveryStatus()));
		assertThat(actual.getProviderStatus(), is(expected.getProviderStatus()));
		assertThat(actual.getOpenMrsId(), is(notNullValue()));
		assertThat(actual.getProviderId(), is(expected.getProviderId()));
		assertThat(actual.getErrorMessage(), is(expected.getErrorMessage()));
	}
}
