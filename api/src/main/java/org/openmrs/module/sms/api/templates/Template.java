package org.openmrs.module.sms.api.templates;

import com.google.gson.JsonSyntaxException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.sms.api.util.TemplateOptionalParameterUtil.shouldParameterBeIncluded;
import static org.openmrs.module.sms.api.util.TemplateOptionalParameterUtil.trimOptionalExpression;
import static org.openmrs.module.sms.api.util.TemplatePlaceholderReplaceUtil.placeholderOrLiteral;

/**
 * Models how we can talk to a specific SMS provider.
 *
 * <p>The Template and related classes have verbose Jackson annotations for clear distinction
 * between properties from JSON files and properties calculated/read from other sources during
 * runtime.
 */
public class Template {

  private static final String FORM_URLENCODED_WITH_CHARSET_UTF_8 =
      "application/x-www-form-urlencoded; charset=utf-8";

  /** Models the handling of outgoing SMS messages. */
  @JsonProperty private Outgoing outgoing;

  /** Models how the status updates from the provider are being handled. */
  @JsonProperty private Status status;

  /** Models how incoming SMS messages are being handled. */
  @JsonProperty private Incoming incoming;

  /** The unique name of the template. */
  @JsonProperty private String name;

  /** Configurable values that the user can edit on the UI. */
  @JsonProperty private List<String> configurables;

  /**
   * Generates an HTTP request for an outgoing SMS from the provided properties.
   *
   * @param props the properties used for building the request
   * @return the HTTP request to execute
   */
  public HttpMethod generateRequestFor(Map<String, Object> props) {
    final HttpMethod httpMethod;

    if (HttpMethodType.POST.equals(outgoing.getRequest().getType())) {
      httpMethod = generatePOSTRequest(props);
    } else {
      httpMethod = new GetMethod(outgoing.getRequest().getUrlPath(props));
    }

    httpMethod.setQueryString(addQueryParameters(props));
    return httpMethod;
  }

  private PostMethod generatePOSTRequest(Map<String, Object> props) {
    final PostMethod postMethod = new PostMethod(outgoing.getRequest().getUrlPath(props));

    if (outgoing.getRequest().getJsonContentType()) {
      final Map<String, String> jsonParams =
          getJsonParameters(outgoing.getRequest().getBodyParameters(), props);

      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode requestBody = objectMapper.getNodeFactory().objectNode();

      for (Map.Entry<String, String> jsonParam : jsonParams.entrySet()) {
        JsonNode jsonParamNode;
        try {
          jsonParamNode = objectMapper.readTree(jsonParam.getValue());
        } catch (JsonSyntaxException | IOException e) {
          jsonParamNode = objectMapper.getNodeFactory().textNode(jsonParam.getValue());
        }
        requestBody.put(jsonParam.getKey(), jsonParamNode);
      }

      String json = requestBody.toString();
      StringRequestEntity requestEntity;

      try {
        requestEntity = new StringRequestEntity(json, MediaType.APPLICATION_JSON, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new IllegalStateException(String.format("Template error: %s: invalid json", name), e);
      }

      postMethod.setRequestEntity(requestEntity);
    } else {
      postMethod.setRequestHeader("Content-Type", FORM_URLENCODED_WITH_CHARSET_UTF_8);
      addBodyParameters(postMethod, props);
    }

    addHeaderParameters(postMethod, props);
    return postMethod;
  }

  /**
   * Formats the recipient list into a single string that can be understood by the provider.
   *
   * @param recipients the list of recipients
   * @return the recipient string for the provider
   */
  public String recipientsAsString(List<String> recipients) {
    return StringUtils.join(recipients.iterator(), outgoing.getRequest().getRecipientsSeparator());
  }

  /** @return the {@link Outgoing} instance used by this template for outgoing SMS messages */
  public Outgoing getOutgoing() {
    return outgoing;
  }

  /** @return the {@link Incoming} instance used by this template for incoming SMS messages */
  public Incoming getIncoming() {
    return incoming;
  }

  /** @return the unique name of this template */
  public String getName() {
    return name;
  }

  /** @param name the unique name of this template */
  public void setName(String name) {
    this.name = name;
  }

  /** @return configurable values that the user can edit on the UI */
  public List<String> getConfigurables() {
    return configurables;
  }

  /** @return the {@link Status} object used for dealing with status updates from the provider */
  public Status getStatus() {
    return status;
  }

  /**
   * @param status the {@link Status} object used for dealing with status updates from the provider
   */
  public void setStatus(Status status) {
    this.status = status;
  }

  /** Reads the default values from OpenMRS settings, updating this template. */
  public void readDefaults() {
    outgoing.readDefaults();
  }

  private NameValuePair[] addQueryParameters(Map<String, Object> props) {
    List<NameValuePair> queryStringValues = new ArrayList<>();
    Map<String, String> queryParameters = outgoing.getRequest().getQueryParameters();
    for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
      String value = placeholderOrLiteral(entry.getValue(), props);
      queryStringValues.add(new NameValuePair(entry.getKey(), value));
    }
    return queryStringValues.toArray(new NameValuePair[0]);
  }

  private Map<String, String> getJsonParameters(
      Map<String, String> bodyParameters, Map<String, Object> props) {
    final Map<String, String> result = new HashMap<>();

    for (Map.Entry<String, String> entry : bodyParameters.entrySet()) {
      final String bodyParametersName = entry.getKey();
      if (shouldParameterBeIncluded(bodyParametersName, props)) {
        final String value = placeholderOrLiteral(entry.getValue(), props);
        result.put(trimOptionalExpression(bodyParametersName), value);
      }
    }

    return result;
  }

  private void addBodyParameters(PostMethod postMethod, Map<String, Object> props) {
    Map<String, String> bodyParameters = outgoing.getRequest().getBodyParameters();
    for (Map.Entry<String, String> entry : bodyParameters.entrySet()) {
      String value = placeholderOrLiteral(entry.getValue(), props);
      postMethod.setParameter(entry.getKey(), value);
    }
  }

  private void addHeaderParameters(PostMethod postMethod, Map<String, Object> props) {
    Map<String, String> headerParameters = outgoing.getRequest().getHeaderParameters();
    for (Map.Entry<String, String> entry : headerParameters.entrySet()) {
      String value = placeholderOrLiteral(entry.getValue(), props);
      postMethod.setRequestHeader(entry.getKey(), value);
    }
  }

  public void setOutgoing(Outgoing outgoing) {
    this.outgoing = outgoing;
  }

  public void setIncoming(Incoming incoming) {
    this.incoming = incoming;
  }

  public void setConfigurables(List<String> configurables) {
    this.configurables = configurables;
  }

  @Override
  public String toString() {
    return "Template{"
        + "outgoing="
        + outgoing
        + ", status="
        + status
        + ", incoming="
        + incoming
        + ", name='"
        + name
        + '\''
        + ", configurables="
        + configurables
        + '}';
  }
}
