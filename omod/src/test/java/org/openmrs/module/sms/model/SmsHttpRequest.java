package org.openmrs.module.sms.model;

import org.apache.commons.codec.binary.Base64;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.ws.rs.core.HttpHeaders;
import java.util.UUID;

public class SmsHttpRequest extends MockHttpServletRequest {

    private boolean validSessionId;

    private boolean generatedSessionID = true;

    @Override
    public String getRequestedSessionId() {
        if (generatedSessionID) {
            return UUID.randomUUID().toString();
        }
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return this.validSessionId;
    }

    public boolean isValidSessionId() {
        return validSessionId;
    }

    public SmsHttpRequest setValidSessionId(boolean validSessionId) {
        this.validSessionId = validSessionId;
        return this;
    }

    public boolean isGeneratedSessionID() {
        return generatedSessionID;
    }

    public SmsHttpRequest setGeneratedSessionID(boolean generatedSessionID) {
        this.generatedSessionID = generatedSessionID;
        return this;
    }

    public SmsHttpRequest setAuthorization(String username, String password) {
        String encoding = Base64.encodeBase64String((username + ":" + password).getBytes());
        this.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
        return this;
    }

    public String getAuthorization() {
        return this.getHeader(HttpHeaders.AUTHORIZATION);
    }
}
