package org.openmrs.module.sms.api.validate.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.configs.Configs;
import org.openmrs.module.sms.api.util.MockedViolationBuilder;
import org.openmrs.module.sms.builder.ConfigBuilder;

import javax.validation.ConstraintValidatorContext;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ConfigsValidatorTest {

	@Mock private ConstraintValidatorContext validatorContext;

	private ConfigsValidator configsValidator = new ConfigsValidator();

	@Before
	public void setUp() {
		doNothing().when(validatorContext).disableDefaultConstraintViolation();
	}

	@Test
	public void shouldReturnValidForEmptyConfigs() {
		assertTrue(configsValidator.isValid(new Configs(), validatorContext));
	}

	@Test
	public void shouldReturnNotValidForEmptyConfig() {
		doReturn(new MockedViolationBuilder())
				.when(validatorContext)
				.buildConstraintViolationWithTemplate(anyString());
		Configs configs = new Configs();
		configs.setConfigList(Collections.singletonList(new Config()));

		assertFalse(configsValidator.isValid(configs, validatorContext));
	}

	@Test
	public void shouldReturnNotValidForBlankTemplate() {
		doReturn(new MockedViolationBuilder())
				.when(validatorContext)
				.buildConstraintViolationWithTemplate(anyString());

		Configs configs = new Configs();
		configs.setConfigList(
				Collections.singletonList(new ConfigBuilder().withTemplateName(null).build()));

		assertFalse(configsValidator.isValid(configs, validatorContext));
	}

	@Test
	public void shouldReturnNotValidForBlankMaxRetries() {
		doReturn(new MockedViolationBuilder())
				.when(validatorContext)
				.buildConstraintViolationWithTemplate(anyString());

		Configs configs = new Configs();
		configs.setConfigList(
				Collections.singletonList(new ConfigBuilder().withMaxRetries(null).build()));

		assertFalse(configsValidator.isValid(configs, validatorContext));
	}

	@Test
	public void shouldReturnNotValidForNegativeMaxRetries() {
		doReturn(new MockedViolationBuilder())
				.when(validatorContext)
				.buildConstraintViolationWithTemplate(anyString());

		Configs configs = new Configs();
		configs.setConfigList(
				Collections.singletonList(new ConfigBuilder().withMaxRetries(-1).build()));

		assertFalse(configsValidator.isValid(configs, validatorContext));
	}

	@Test
	public void shouldReturnNotValidForBlankSplitHeader() {
		doReturn(new MockedViolationBuilder())
				.when(validatorContext)
				.buildConstraintViolationWithTemplate(anyString());

		Configs configs = new Configs();
		configs.setConfigList(
				Collections.singletonList(new ConfigBuilder().withSplitHeader(null).build()));

		assertFalse(configsValidator.isValid(configs, validatorContext));
	}

	@Test
	public void shouldReturnNotValidForBlankSplitFooter() {
		doReturn(new MockedViolationBuilder())
				.when(validatorContext)
				.buildConstraintViolationWithTemplate(anyString());

		Configs configs = new Configs();
		configs.setConfigList(
				Collections.singletonList(new ConfigBuilder().withSplitFooter(null).build()));

		assertFalse(configsValidator.isValid(configs, validatorContext));
	}

	@Test
	public void shouldReturnValidForValidSplitFooter() {
		doReturn(new MockedViolationBuilder())
				.when(validatorContext)
				.buildConstraintViolationWithTemplate(anyString());

		Configs configs = new Configs();
		configs.setConfigList(
				Collections.singletonList(new ConfigBuilder().withSplitFooter("configs[%d].splitFooter").build()));

		assertTrue(configsValidator.isValid(configs, validatorContext));
	}

	@Test
	public void shouldReturnNotValidForBlankEntity() {
		doReturn(new MockedViolationBuilder())
				.when(validatorContext)
				.buildConstraintViolationWithTemplate(anyString());
		Configs configs = new Configs();
		configs.setConfigList(Collections.singletonList(new Config()));

		assertFalse(configsValidator.isValid(configs, validatorContext));
	}
}
