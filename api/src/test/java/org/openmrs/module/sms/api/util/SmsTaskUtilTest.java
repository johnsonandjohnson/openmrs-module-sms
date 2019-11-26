package org.openmrs.module.sms.api.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SmsTaskUtilTest {

	private static final String STRING_62CHARS = RandomStringUtils.random(62, true, false);

	@Test
	public void shouldProperlyGenerateTaskName() {
		assertThat(SmsTaskUtil.generateTaskName("subject", "jobId"), equalTo("subject-jobId"));
	}

	@Test
	public void shouldShortenTaskNameIfLengthMoreThanMax() {
		assertThat(SmsTaskUtil.generateTaskName(STRING_62CHARS, "").length(), equalTo(SmsTaskUtil.NAME_MAX_LENGTH));
	}
}
