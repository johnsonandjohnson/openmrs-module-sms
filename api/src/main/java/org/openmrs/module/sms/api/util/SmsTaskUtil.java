package org.openmrs.module.sms.api.util;

public final class SmsTaskUtil {

	public static final int NAME_MAX_LENGTH = 50;

	public static String generateTaskName(String subject, String jobId) {
		String name = String.format("%s-%s", subject, jobId);
		return name.length() > NAME_MAX_LENGTH ? name.substring(0, NAME_MAX_LENGTH) : name;
	}

	private SmsTaskUtil() {
	}
}
