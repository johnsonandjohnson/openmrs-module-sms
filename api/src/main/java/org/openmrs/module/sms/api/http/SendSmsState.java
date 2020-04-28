package org.openmrs.module.sms.api.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.templates.Template;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.openmrs.module.sms.api.util.Constants.SMS_DEFAULT_RETRY_COUNT;

public class SendSmsState implements Serializable {

    private static final long serialVersionUID = 2632585637814840133L;
    private static final Log LOGGER = LogFactory.getLog(SendSmsState.class);
    private static final long MINUTE = 60000;
    private Template template;
    private HttpClient commonsHttpClient;
    private HttpMethod httpMethod;
    private Integer httpStatus;
    private String httpResponse;
    private String errorMessage;

    public SendSmsState setCommonsHttpClient(HttpClient commonsHttpClient) {
        this.commonsHttpClient = commonsHttpClient;
        return this;
    }

    public SendSmsState setTemplate(Template template) {
        this.template = template;
        return this;
    }

    public SendSmsState setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public SendSmsState setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public SendSmsState setHttpResponse(String httpResponse) {
        this.httpResponse = httpResponse;
        return this;
    }

    public String getHttpResponse() {
        return httpResponse;
    }

    public SendSmsState setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public SendSmsState build() {
        boolean shouldRetry = true;
        int retryCount = 0;
        while (shouldRetry) {
            shouldRetry = false;
            try {
                httpStatus = commonsHttpClient.executeMethod(httpMethod);
                httpResponse = httpMethod.getResponseBodyAsString();
            } catch (UnknownHostException e) {
                errorMessage = String.format("Network connectivity issues or problem with '%s' template? %s",
                        template.getName(), e.toString());
            } catch (IllegalArgumentException | IOException | IllegalStateException e) {
                String msg = String.format("Problem with '%s' template? %s", template.getName(), e.toString());
                if (SocketException.class.isAssignableFrom(e.getClass()) && retryCount < SMS_DEFAULT_RETRY_COUNT) {
                    LOGGER.warn(msg);
                    sleep(MINUTE);
                    shouldRetry = true;
                    retryCount++;
                } else {
                    errorMessage = msg;
                }
            } finally {
                if (httpMethod != null) {
                    httpMethod.releaseConnection();
                }
            }
        }
        return this;
    }

    private void sleep(long milliseconds) {
        LOGGER.debug(String.format("Sleeping thread id %d for %d ms", Thread.currentThread().getId(), milliseconds));
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
