package org.openmrs.module.sms.api.util;

import org.openmrs.module.sms.api.audit.constants.DeliveryStatuses;
import org.openmrs.module.sms.api.templates.Template;

import java.util.HashMap;
import java.util.Map;

/**
 * The IncomingMessageAccessor Class.
 *
 * <p>Allows to read incoming message provider data according to configuration in a {@link
 * Template}.
 */
public class IncomingMessageAccessor {

  private final Template template;
  private final Map<String, String> providerData;

  /**
   * Initialize new instance of the Converter.
   *
   * @param template the template with configuration, not null
   * @param providerData the data from provider (usually the request body or/and request query
   *     parameters)
   */
  public IncomingMessageAccessor(Template template, Map<String, String> providerData) {
    this.template = template;
    this.providerData = new HashMap<>(providerData);
  }

  public String getSender() {
    String sender = null;
    if (providerData.containsKey(template.getIncoming().getSenderKey())) {
      sender = providerData.get(template.getIncoming().getSenderKey());

      if (template.getIncoming().hasSenderRegex()) {
        sender = template.getIncoming().extractSender(sender);
      }
    }
    return sender;
  }

  public String getMessage() {
    String message = null;

    if (providerData.containsKey(template.getIncoming().getMessageKey())) {
      message = providerData.get(template.getIncoming().getMessageKey());

      if (template.getIncoming().hasMessageRegex()) {
        message = template.getIncoming().extractMessage(message);
      }
    }

    return message;
  }

  public String getMsgId() {
    return providerData.get(template.getIncoming().getMsgIdKey());
  }

  public String getStatus() {
    return template.getStatus().hasStatusKey()
            && providerData.containsKey(template.getStatus().getStatusKey())
        ? providerData.get(template.getStatus().getStatusKey())
        : DeliveryStatuses.RECEIVED;
  }
}
