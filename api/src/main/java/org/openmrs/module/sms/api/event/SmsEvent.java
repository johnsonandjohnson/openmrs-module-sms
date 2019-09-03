package org.openmrs.module.sms.api.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.openmrs.module.sms.api.util.Constants;
import org.openmrs.module.sms.api.util.SmsEventParams;
import org.openmrs.module.sms.api.util.SmsUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsEvent {
	private String subject;
	private Map<String, Object> parameters;

	public SmsEvent(String subject, Map<String, Object> parameters) {
		this.subject = subject;
		this.parameters = parameters;
	}

	public SmsEvent(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getJobId() {
		return (String) getParameters().get(Constants.PARAM_JOB_ID);
	}

	public String generateTaskName() {
		return SmsUtil.generateTaskName(getSubject(), getJobId());
	}

	public boolean paramsContainKey(String key) {
		return getParameters().containsKey(key);
	}

	public Integer getFailureCount() {
		return (Integer) getParameters().get(SmsEventParams.FAILURE_COUNT);
	}

	public String getMotechId() {
		return (String) getParameters().get(SmsEventParams.MOTECH_ID);
	}

	public String getProviderId() {
		return (String) getParameters().get(SmsEventParams.PROVIDER_MESSAGE_ID);
	}

	public String getConfigParam() {
		return (String) getParameters().get(SmsEventParams.CONFIG);
	}

	public Map<String, String> getCustomParams() {
		return (Map<String, String>) getParameters().get(SmsEventParams.CUSTOM_PARAMS);
	}

	public List<String> getRecipients() {
		return (List<String>) getParameters().get(SmsEventParams.RECIPIENTS);
	}

	public String getMessage() {
		return (String) getParameters().get(SmsEventParams.MESSAGE);
	}

	public DateTime getDeliveryTime() {
		return (DateTime) getParameters().get(SmsEventParams.DELIVERY_TIME);
	}

	public Map<String, String> convertProperties() {
		Map<String, String> result = new HashMap<>();

		for (String key : getParameters().keySet()) {
			if (SmsEventParams.CUSTOM_PARAMS.equals(key)) {

			} else if (SmsEventParams.RECIPIENTS.equals(key)) {

			} else {
				result.put(key, getParameters().get(key).toString());
			}
		}

		return result;
	}

	public static Map<String, Object> convertProperties(Map<String, String> properties) {
		Map<String, Object> result = new HashMap<>();

		for (String key : properties.keySet()) {
			if (SmsEventParams.CUSTOM_PARAMS.equals(key)) {

			} else if (SmsEventParams.RECIPIENTS.equals(key)) {

			} else if (SmsEventParams.DELIVERY_TIME.equals(key)) {

			} else if (SmsEventParams.FAILURE_COUNT.equals(key)) {

			} else {
				result.put(key, properties.get(key));
			}
		}

		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
