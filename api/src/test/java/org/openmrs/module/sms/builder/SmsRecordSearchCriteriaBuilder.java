package org.openmrs.module.sms.builder;

import org.hibernate.criterion.Order;
import org.openmrs.module.sms.TestConstants;
import org.openmrs.module.sms.api.audit.SmsDirection;
import org.openmrs.module.sms.api.audit.SmsRecordSearchCriteria;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatuses;
import org.openmrs.module.sms.api.web.Interval;

import java.util.HashSet;
import java.util.Set;

public class SmsRecordSearchCriteriaBuilder extends AbstractBuilder<SmsRecordSearchCriteria> {

    private Interval interval;
    private String config;
    private HashSet<SmsDirection> directions;
    private Set<String> statuses;
    private String providerStatus;
    private Order order;
    private String phoneNumber;
    private String messageContent;
    private String errorMessage;


    public SmsRecordSearchCriteriaBuilder() {
        this.interval = new IntervalBuilder().build();
        this.config = TestConstants.CONFIG;
        this.directions = buildDirections();
        this.statuses = buildStatuses();
        this.providerStatus = TestConstants.PROVIDER_STATUS;
        this.order = Order.desc(TestConstants.SORT_COLUMN);
        this.phoneNumber = TestConstants.PHONE_NUMBER;
        this.messageContent = TestConstants.MESSAGE_CONTENT;
        this.errorMessage = TestConstants.ERROR_MESSAGE;
    }

    @Override
    public SmsRecordSearchCriteria build() {
        SmsRecordSearchCriteria searchCriteria = new SmsRecordSearchCriteria();
        searchCriteria.withTimestampRange(this.interval);
        searchCriteria.withConfig(this.config);
        searchCriteria.withSmsDirections(this.directions);
        searchCriteria.withDeliveryStatuses(this.statuses);
        searchCriteria.withProviderStatus(this.providerStatus);
        searchCriteria.withOrder(this.order);
        searchCriteria.withPhoneNumber(this.phoneNumber);
        searchCriteria.withMessageContent(this.messageContent);
        searchCriteria.withErrorMessage(this.errorMessage);
        return searchCriteria;
    }

    @Override
    public SmsRecordSearchCriteria buildAsNew() {
        return null;
    }

    public Interval getInterval() {
        return interval;
    }

    public SmsRecordSearchCriteriaBuilder withInterval(Interval interval) {
        this.interval = interval;
        return this;
    }

    public String getConfig() {
        return config;
    }

    public SmsRecordSearchCriteriaBuilder withConfig(String config) {
        this.config = config;
        return this;
    }

    public HashSet<SmsDirection> getDirections() {
        return directions;
    }

    public SmsRecordSearchCriteriaBuilder withDirections(HashSet<SmsDirection> directions) {
        this.directions = directions;
        return this;
    }

    public Set<String> getStatuses() {
        return statuses;
    }

    public SmsRecordSearchCriteriaBuilder withStatuses(Set<String> statuses) {
        this.statuses = statuses;
        return this;
    }

    public String getProviderStatus() {
        return providerStatus;
    }

    public SmsRecordSearchCriteriaBuilder withProviderStatus(String providerStatus) {
        this.providerStatus = providerStatus;
        return this;
    }

    public Order getOrder() {
        return order;
    }

    public SmsRecordSearchCriteriaBuilder withOrder(Order order) {
        this.order = order;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public SmsRecordSearchCriteriaBuilder withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public SmsRecordSearchCriteriaBuilder withMessageContent(String messageContent) {
        this.messageContent = messageContent;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public SmsRecordSearchCriteriaBuilder withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    private HashSet<SmsDirection> buildDirections() {
        HashSet<SmsDirection> result = new HashSet<>();
        result.add(SmsDirection.OUTBOUND);
        result.add(SmsDirection.INBOUND);
        return result;
    }

    private Set<String> buildStatuses() {
        HashSet<String> results = new HashSet<>();
        results.add(DeliveryStatuses.DISPATCHED);
        results.add(DeliveryStatuses.ABORTED);
        return results;
    }
}
