package org.openmrs.module.sms.api.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.module.sms.api.util.Constants;
import org.openmrs.module.sms.api.util.SmsUtil;

import java.util.HashMap;
import java.util.Map;

public class SmsEvent {
	private String subject;
	private Map<String, Object> parameters;

	public SmsEvent(String subject, Map<String, Object> parameters) {
		this.subject = subject;
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

	public Map<String, String> convertProperties() {
		Map<String, String> result = new HashMap<>();

		for (String key : getParameters().keySet()) {
			result.put(key, (String) getParameters().get(key));
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
