package org.openmrs.module.sms.api.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Map;

/**
 * The AutomaticResponseData Class.
 *
 * <p>The JSON-serializable class to store complete information about message created by automatic
 * response script.
 *
 * <p>The {@code customParameters} are passed as-is, and their exact meaning depends on the SMS
 * Configuration Template.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutomaticResponseData {
  private String message;
  private Map<String, Object> customParameters;

  /** @return the message text, never null */
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  /** @return the Map of custom parameters passed together with massage, never null */
  public Map<String, Object> getCustomParameters() {
    return customParameters;
  }

  public void setCustomParameters(Map<String, Object> customParameters) {
    this.customParameters = customParameters;
  }
}
