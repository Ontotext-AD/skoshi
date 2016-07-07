package com.ontotext.skoshi.error;

/**
 * This exception is throws when trying to perform an operation over an com.ontotext.skoshi.model.entity
 * that does not exist in the DB.
 *
 * @author philip
 */
public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 3190386356667612842L;

	public NotFoundException() {
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
