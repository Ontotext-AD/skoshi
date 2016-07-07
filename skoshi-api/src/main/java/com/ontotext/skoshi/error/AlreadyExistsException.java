package com.ontotext.skoshi.error;

/**
 * The purpose of this exception is to be thrown when trying to create
 * an com.ontotext.skoshi.model.entity with an ID that already exists in the database.
 *
 * @author philip
 */
public class AlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 3836377406495210193L;

	public AlreadyExistsException() {
	}

	public AlreadyExistsException(String message) {
		super(message);
	}

	public AlreadyExistsException(Throwable cause) {
		super(cause);
	}

	public AlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyExistsException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
