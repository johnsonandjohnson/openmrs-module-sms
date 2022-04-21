package org.openmrs.module.sms.api.exception;

public class SmsRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 4513932642755534409L;

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
