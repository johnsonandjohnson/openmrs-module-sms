package org.openmrs.module.sms.api.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.http.SmsHttpService;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.module.sms.api.util.SmsEventParams;
import org.openmrs.module.sms.api.util.SmsEventSubjects;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatusSmsEventListener extends AbstractSmsEventListener {

	private static final Log LOGGER = LogFactory.getLog(StatusSmsEventListener.class);

	@Override
	public String[] getSubjects() {
		return new String[] { SmsEventSubjects.PENDING, SmsEventSubjects.SCHEDULED, SmsEventSubjects.RETRYING };
	}

	@Override
	protected void handleEvent(Map<String, Object> properties) {
		LOGGER.info(String.format("Handling external event %s: %s", Arrays.toString(getSubjects()),
				properties.get("message").toString().replace("\n", "\\n")));
		properties = convertProperties(properties);
		getComponent("sms.SmsHttpService", SmsHttpService.class)
				.send(new OutgoingSms(new SmsEvent(properties)));
	}

	private Map<String, Object> convertProperties(Map<String, Object> properties) {
		Map<String, Object> result = new LinkedHashMap<>();
		for (String key : properties.keySet()) {
				if (SmsEventParams.TIMESTAMP.equals(key)) {
						result.put(key, DateUtil.parse((String) properties.get(SmsEventParams.TIMESTAMP)));
					} else {
						result.put(key, properties.get(key));
					}
			}
		return result;
	}
}
