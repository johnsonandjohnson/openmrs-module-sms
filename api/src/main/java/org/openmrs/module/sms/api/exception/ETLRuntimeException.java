package org.openmrs.module.sms.api.exception;

public class ETLRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 5343408438923419174L;

	public ETLRuntimeException(Throwable throwable) {
		super(throwable);
	}

	public ETLRuntimeException(String message) {
		super(message);
	}

	public ETLRuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
