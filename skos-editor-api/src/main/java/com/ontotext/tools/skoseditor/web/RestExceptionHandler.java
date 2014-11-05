package com.ontotext.tools.skoseditor.web;

import com.ontotext.openpolicy.error.AlreadyExistsException;
import com.ontotext.openpolicy.error.DataAccessException;
import com.ontotext.openpolicy.error.NotFoundException;
import com.ontotext.openpolicy.error.OpenPolicyException;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import java.util.ConcurrentModificationException;

// source:  http://www.javacodegeeks.com/2013/02/exception-handling-for-rest-with-spring-3-2.html

/**
 * This handler serves the sole purpose to intercept exceptions (mostly runtime)
 * and to create a standard REST web response with the appropriate HTTP response code.
 * If not handled by this object, exceptions will propagate to Spring, which
 * then returns a standard page with the exception's stack trace.
 * It is good that all exceptions thrown in the system are caught here.
 * The services' descriptions show what error status codes can be returned
 * in what situations. Those error codes are returned by this handler,
 * when catching the corresponding java exception.
 */
@ControllerAdvice
public class RestExceptionHandler {

	private final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

	@ExceptionHandler(DataAccessException.class)
	protected ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
		log.error("Caught exception!", ex);
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NullPointerException.class)
	protected ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
		log.error("Caught exception!", ex);
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(IllegalStateException.class)
	protected ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
		log.error("Caught exception!", ex);
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({
            IllegalArgumentException.class,
            MultipartException.class})
	protected ResponseEntity<String> handleBadRequests(Exception ex) {
		log.error("Caught exception!", ex);
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(org.springframework.core.convert.ConversionFailedException.class)
	protected ResponseEntity<String> handleConversionFailedException(org.springframework.core.convert.ConversionFailedException ex) {
		log.error("Caught exception!", ex);
		return new ResponseEntity<>("Invalid parameter. " + ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	protected ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
		log.error("Caught exception!", ex);
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(NotImplementedException.class)
	protected ResponseEntity<String> handleNotImplementedException(NotImplementedException ex) {
		log.error("Caught exception!", ex);
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
	}

	@ExceptionHandler(OpenPolicyException.class)
	protected ResponseEntity<String> handleDocumentManagementException(OpenPolicyException ex) {
		log.error("Caught exception!", ex);
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AlreadyExistsException.class)
	protected ResponseEntity<String> handleAlreadyExistsException(AlreadyExistsException ex) {
		log.error("Caught exception!", ex);
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(UnsupportedOperationException.class)
	protected ResponseEntity<String> handleUnsupportedOperationException(UnsupportedOperationException ex) {
		log.error("Caught exception!", ex);
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConcurrentModificationException.class)
	protected ResponseEntity<String> handleConcurrentModificationException(ConcurrentModificationException ex) {
		log.error("Caught exception!", ex);
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.LOCKED);
	}
}
