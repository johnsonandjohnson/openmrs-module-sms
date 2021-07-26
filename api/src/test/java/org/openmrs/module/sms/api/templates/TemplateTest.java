package org.openmrs.module.sms.api.templates;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TemplateTest {

    private static final String PROVIDER_STATUS_TEMPLATE = "templates/provider-status-template.json";
    private static final String NEXMO_RESPONSE_JSON = "responses/nexmo-response.json";

    private static final String TURN_IO_TEMPLATE = "templates/turnIO-template.json";
    private static final String TURN_IO_RESPONSE_JSON = "responses/turnIO-response.json";

    private static final String TURN_IO_PROVIDER_ID = "gBEGkYiEB1VXAglK1ZE_qA1YKPrU";

    @Test
    public void shouldGeneratePostMethod() throws IOException {
        Template template = loadVotoTemplate();

        String expectedJson = "{\"status_callback_url\":\"http://:someUrl\",\"subscribers\":[{\"phone\":\"48700123123\",\"language\":\"en\"}]}";

        Map<String, String> props = new HashMap<>();
        props.put("message", "message");
        props.put("recipients", "1");
        props.put("callback", "http://:someUrl");
        props.put("subscribers", "[{\"phone\":\"48700123123\",\"language\":\"en\"}]");

        HttpMethod httpMethod = template.generateRequestFor(props);

        RequestEntity requestEntity = ((PostMethod) httpMethod).getRequestEntity();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        requestEntity.writeRequest(bos);
        String json = new String(bos.toByteArray(), StandardCharsets.UTF_8);

        assertEquals(expectedJson, json);
    }

    @Test
    public void shouldExtractProviderStatus() throws IOException {
        Template template = loadTemplate(PROVIDER_STATUS_TEMPLATE);
        String response = loadResponse(NEXMO_RESPONSE_JSON);

        String providerStatus = template.getOutgoing().getResponse().extractProviderStatus(response);

        assertEquals("0", providerStatus);
    }

    @Test
    public void shouldExtractNullIfProviderStatusNotSet() throws IOException {
        Template template = loadVotoTemplate();
        String response = loadResponse(NEXMO_RESPONSE_JSON);

        String providerStatus = template.getOutgoing().getResponse().extractProviderStatus(response);

        assertNull(providerStatus);
    }

    @Test
    public void shouldExtractProviderIDForTurnIOWithUnderscore() throws IOException {
        Template template = loadTemplate(TURN_IO_TEMPLATE);
        String response = loadResponse(TURN_IO_RESPONSE_JSON);

        String providerID = template.getOutgoing().getResponse().extractSingleSuccessMessageId(response);

        assertEquals(TURN_IO_PROVIDER_ID, providerID);
    }

    private Template loadVotoTemplate() {
        String jsonTemplate = "{\n" +
                "        \"name\":\"Voto\",\n" +
                "        \"configurables\": [\n" +
                "            \"api_key\"\n" +
                "        ],\n" +
                "        \"outgoing\":{\n" +
                "            \"maxSmsSize\":\"160\",\n" +
                "            \"millisecondsBetweenMessages\":\"1\",\n" +
                "            \"exponentialBackOffRetries\":\"true\",\n" +
                "            \"maxRecipient\":\"1\",\n" +
                "            \"hasAuthentication\":\"false\",\n" +
                "            \"request\":{\n" +
                "                \"type\":\"POST\",\n" +
                "                \"urlPath\":\"https://url\",\n" +
                "                \"jsonContentType\":\"true\",\n" +
                "                \"bodyParameters\":{\n" +
                "                    \"status_callback_url\":\"[callback]\",\n" +
                "                    \"subscribers\":\"[subscribers]\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"response\":{\n" +
                "            }\n" +
                "        },\n" +
                "        \"status\":{\n" +
                "        },\n" +
                "        \"incoming\":{\n" +
                "        }\n" +
                "    }";
        Gson gson = new Gson();
        return gson.fromJson(jsonTemplate, new TypeToken<Template>() {
        }.getType());
    }

    private Template loadTemplate(String templateFile) throws IOException {
        try (InputStream templateStream = getClass().getClassLoader().getResourceAsStream(templateFile)) {

            if (templateStream == null) {
                throw new IOException("Cannot read template file: " + templateFile);
            }

            String jsonTemplate = IOUtils.toString(templateStream);

            Gson gson = new Gson();

            return gson.fromJson(jsonTemplate, new TypeToken<Template>() {
            } .getType());
        }
    }

    private String loadResponse(String responseFile) throws IOException {
        try (InputStream responseStream = getClass().getClassLoader().getResourceAsStream(responseFile)) {
            if (responseStream == null) {
                throw new IOException("Cannot read response file: " + responseFile);
            }
            return IOUtils.toString(responseStream);
        }
    }
}
