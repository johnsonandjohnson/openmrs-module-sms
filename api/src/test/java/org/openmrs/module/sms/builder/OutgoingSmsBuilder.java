package org.openmrs.module.sms.builder;

import org.openmrs.module.sms.api.service.OutgoingSms;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutgoingSmsBuilder extends AbstractBuilder<OutgoingSms> {

    private static final String USERNAME_PROP = "username";
    private static final String PASSWORD_PROP = "password";
    private static final String PASSWORD = "Admin123";
    private static final String USERNAME = "admin";
    private String config;
    private List<String> recipients;
    private String providerId;
    private Integer failureCount;
    private String message;
    private boolean withAuthenticationParams;
    private Date deliveryTime;

    public OutgoingSmsBuilder() {
        this.config = "nexmo";
        this.recipients = Collections.singletonList("35235567243");
        this.providerId = "nexmo";
        this.failureCount = 2;
        this.message = "test message";
        this.withAuthenticationParams = true;
        this.deliveryTime = null;
    }

    @Override
    public OutgoingSms build() {
        OutgoingSms sms = new OutgoingSms();
        sms.setConfig(this.config);
        sms.setRecipients(this.recipients);
        sms.setProviderId(this.providerId);
        sms.setFailureCount(this.failureCount);
        sms.setCustomParams(buildCustomProps());
        sms.setMessage(this.message);
        sms.setDeliveryTime(this.deliveryTime);
        return sms;
    }

    @Override
    public OutgoingSms buildAsNew() {
        return build();
    }

    public String getConfig() {
        return config;
    }

    public OutgoingSmsBuilder withConfig(String config) {
        this.config = config;
        return this;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public OutgoingSmsBuilder withRecipients(List<String> recipients) {
        this.recipients = recipients;
        return this;
    }

    public String getProviderId() {
        return providerId;
    }

    public OutgoingSmsBuilder withProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public OutgoingSmsBuilder withFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public OutgoingSmsBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isWithAuthenticationParams() {
        return withAuthenticationParams;
    }

    public OutgoingSmsBuilder withWithAuthenticationParams(boolean withAuthenticationParams) {
        this.withAuthenticationParams = withAuthenticationParams;
        return this;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public OutgoingSmsBuilder withDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
        return this;
    }

    private Map<String, String> buildCustomProps() {
        HashMap<String, String> props = new HashMap<>();
        if (withAuthenticationParams) {
            props.put(USERNAME_PROP, USERNAME);
            props.put(PASSWORD_PROP, PASSWORD);
        }
        return props;
    }
}
