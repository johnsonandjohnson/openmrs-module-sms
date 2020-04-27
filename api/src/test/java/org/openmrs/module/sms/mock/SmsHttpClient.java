package org.openmrs.module.sms.mock;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

public class SmsHttpClient extends HttpClient {

    private static final String ERROR_MESSAGE = "{\n" +
            "    \"message-count\": \"1\",\n" +
            "    \"messages\": [{\n" +
            "        \"status\": \"4\",\n" +
            "        \"error-text\": \"Bad Credentials\"\n" +
            "    }]\n" +
            "}";
    private static final String SUCCESS_MESSAGE = "{\n" +
            "      \"to\": \"447700900000\",\n" +
            "      \"status\": \"0\",\n" +
            "      \"remaining-balance\": \"3.14159265\",\n" +
            "      \"message-price\": \"0.03330000\",\n" +
            "      \"network\": \"12345\",\n" +
            "      \"client-ref\": \"my-personal-reference\",\n" +
            "      \"account-ref\": \"customer1234\"\n" +
            "      %s" +
            "    }";
    private static final String MESSAGE_ID_PARAM = ",\"message-id\": \"0A0000000123ABCD1\"\n";
    private boolean successProcessed;
    private boolean messageId;

    public SmsHttpClient() {
        this.messageId = true;
        this.successProcessed = true;
    }

    @Override
    public int executeMethod(HttpMethod method) {
        if (successProcessed) {
            return processWithSuccess(method);
        }
        return processWithFailure(method);
    }

    private int processWithSuccess(HttpMethod method) {
        ReflectionTestUtils.setField(method, "responseBody",
                getSuccessMessage().getBytes());
        return HttpStatus.SC_OK;
    }

    private String getSuccessMessage() {
        StringBuilder additionalProps = new StringBuilder();
        if (messageId) {
            additionalProps.append(MESSAGE_ID_PARAM);
        }
        return String.format(SUCCESS_MESSAGE, additionalProps.toString());
    }

    private int processWithFailure(HttpMethod method) {
        ReflectionTestUtils.setField(method, "responseBody",
                ERROR_MESSAGE.getBytes());
        return HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }

    public boolean isSuccessProcessed() {
        return successProcessed;
    }

    public SmsHttpClient withSuccessProcessed(boolean successProcessed) {
        this.successProcessed = successProcessed;
        return this;
    }

    public boolean isMessageId() {
        return messageId;
    }

    public SmsHttpClient withMessageId(boolean withMessageId) {
        this.messageId = withMessageId;
        return this;
    }
}
