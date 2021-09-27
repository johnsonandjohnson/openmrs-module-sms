package org.openmrs.module.sms.api.web.dto;

public class SmsRecordDTO extends BaseDTO {

    private static final long serialVersionUID = 8104465406110473101L;

    private Integer id;

    private String errorMessage;

    private String providerStatus;

    private String openMrsId;

    private String providerId;

    private String deliveryStatus;

    private String messageContent;

    private String timestamp;

    private String config;

    private String smsDirection;

    private String phoneNumber;

    private String modificationDate;

    private String creationDate;

    private String modifiedBy;

    private String creator;

    public SmsRecordDTO() {
    }

    //CHECKSTYLE:OFF: ParameterNumber
    public SmsRecordDTO(Integer id, String errorMessage, String providerStatus, String openMrsId, String providerId,
                        String deliveryStatus, String messageContent, String timestamp, String config, String smsDirection,
                        String phoneNumber, String modificationDate, String creationDate, String modifiedBy, String creator) {
        this.id = id;
        this.errorMessage = errorMessage;
        this.providerStatus = providerStatus;
        this.openMrsId = openMrsId;
        this.providerId = providerId;
        this.deliveryStatus = deliveryStatus;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        this.config = config;
        this.smsDirection = smsDirection;
        this.phoneNumber = phoneNumber;
        this.modificationDate = modificationDate;
        this.creationDate = creationDate;
        this.modifiedBy = modifiedBy;
        this.creator = creator;
    }
    //CHECKSTYLE:ON: ParameterNumber

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getProviderStatus() {
        return providerStatus;
    }

    public void setProviderStatus(String providerStatus) {
        this.providerStatus = providerStatus;
    }

    public String getOpenMrsId() {
        return openMrsId;
    }

    public void setOpenMrsId(String openMrsId) {
        this.openMrsId = openMrsId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getSmsDirection() {
        return smsDirection;
    }

    public void setSmsDirection(String smsDirection) {
        this.smsDirection = smsDirection;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
