package org.openmrs.module.sms.api.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtil {

	private static final Log LOGGER = LogFactory.getLog(DateUtil.class);

	private static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	private static final String DEFAULT_TIME_ZONE = "UTC";

	public static Date parse(String dateTime) {
		return parse(dateTime, null);
	}

	public static Date parse(String dateTime, String pattern) {
		if (StringUtils.isBlank(pattern)) {
			pattern = ISO_DATE_TIME_FORMAT;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date result = null;
		try {
			result = simpleDateFormat.parse(dateTime);
		} catch (ParseException e) {
			LOGGER.error(String.format("Could not parse `%s` date using `%s` pattern", dateTime, pattern));
		}
		return result;
	}

	public static Date now() {
		return getDateWithDefaultTimeZone(new Date());
	}

	public static Date plusDays(Date date, int duration) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, duration);
		return calendar.getTime();
	}

	public static Date getDateWithDefaultTimeZone(Date timestamp) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
		calendar.setTime(timestamp);
		return calendar.getTime();
	}

	public static String getDateWithLocalTimeZone(Date timestamp) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_TIME_FORMAT);
		simpleDateFormat.setTimeZone(getLocalTimeZone());
		return simpleDateFormat.format(timestamp);
	}

	public static String dateToString(Date date, String timeZone) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_TIME_FORMAT);
		if (StringUtils.isNotBlank(timeZone)) {
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		return simpleDateFormat.format(date);
	}

	public static String dateToString(Date date) {
		return dateToString(date, DEFAULT_TIME_ZONE);
	}

	public static TimeZone getLocalTimeZone() {
		return TimeZone.getDefault();
	}

	private DateUtil() { }
}
