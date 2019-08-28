package org.openmrs.module.sms.api.util;

public final class SmsUtil {

	private static final String SUFFIX_SMS_TASK = "-runonce";

	public static String generateTaskName(String subject, String jobId) {
		return String.format("%s-%s%s", subject, jobId, SUFFIX_SMS_TASK);
	}

	private SmsUtil() {
	}
}
