package org.openmrs.module.sms.api.util;

import java.util.Arrays;
import java.util.Date;

public final class SmsUtil {

	public static String generateTaskName(String subject, String jobId) {
		return String.format("%s-%s", subject, jobId);
	}

	private SmsUtil() {
	}
}
