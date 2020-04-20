package org.openmrs.module.sms.api.exception;

public class SmsRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 5343408438923419174L;

    public SmsRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public SmsRuntimeException(String message) {
        super(message);
    }

    public SmsRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
