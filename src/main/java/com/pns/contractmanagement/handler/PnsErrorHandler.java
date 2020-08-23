package com.pns.contractmanagement.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.ErrorResponse;
import com.pns.contractmanagement.model.ImmutableErrorResponse;

public class PnsErrorHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PnsErrorHandler.class);

	@ExceptionHandler(PnsException.class)
	public ResponseEntity<ErrorResponse> handlePnsException(PnsException exception) {
		LOGGER.error("Exception Occured while processing request", exception);
		final ImmutableErrorResponse errorResponse = ImmutableErrorResponse.builder().message(exception.getMessage())
				.build();
		return ResponseEntity.status(exception.getError().getStatus()).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception exception) {
		LOGGER.error("Exception Occured while processing request", exception);
		final ImmutableErrorResponse errorResponse = ImmutableErrorResponse.builder().message(
				exception.getMessage() != null ? exception.getMessage() : PnsError.SYSTEM_ERROR.getGenericMessage())
				.build();
		return ResponseEntity.status(PnsError.SYSTEM_ERROR.getStatus()).body(errorResponse);
	}

}
