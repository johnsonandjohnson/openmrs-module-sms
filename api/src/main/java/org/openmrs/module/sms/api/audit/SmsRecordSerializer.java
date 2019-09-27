package org.openmrs.module.sms.api.audit;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.openmrs.User;
import org.openmrs.module.sms.api.util.DateUtil;

import java.io.IOException;

public class SmsRecordSerializer extends JsonSerializer<SmsRecord> {

	@Override
	public void serialize(SmsRecord value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException {
		jgen.writeStartObject();
		jgen.writeNumberField("id", value.getId());
		jgen.writeStringField("errorMessage", value.getErrorMessage());
		jgen.writeStringField("providerStatus", value.getProviderStatus());
		jgen.writeStringField("motechId", value.getMotechId());
		jgen.writeStringField("providerId", value.getProviderId());
		jgen.writeStringField("deliveryStatus", value.getDeliveryStatus());
		jgen.writeStringField("messageContent", value.getMessageContent());
		jgen.writeStringField("timestamp", DateUtil.dateToString(value.getTimestamp()));
		jgen.writeStringField("config", value.getConfig());
		SmsDirection direction = value.getSmsDirection();
		jgen.writeStringField("smsDirection", direction != null ? direction.name() : null);
		jgen.writeStringField("phoneNumber", value.getPhoneNumber());
		jgen.writeStringField("modificationDate", DateUtil.dateToString(value.getDateChanged()));
		jgen.writeStringField("creationDate", DateUtil.dateToString(value.getDateCreated()));
		User modifiedBy = value.getChangedBy();
		jgen.writeStringField("modifiedBy", modifiedBy != null ? modifiedBy.getUsername() : null);
		User creator = value.getCreator();
		jgen.writeStringField("creator", creator != null ? creator.getUsername() : null);
		jgen.writeEndObject();
	}
}
