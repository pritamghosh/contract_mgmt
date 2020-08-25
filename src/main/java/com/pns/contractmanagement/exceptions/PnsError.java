package com.pns.contractmanagement.exceptions;

import org.springframework.http.HttpStatus;

/**
 *
 */
public enum PnsError {
	SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "System Error"),
	NOT_FOUND(HttpStatus.NOT_FOUND, "Resource Not Found"), DUPLICTE_RECORD(HttpStatus.BAD_REQUEST, "Duplicate Record"),
	WARNING(HttpStatus.INTERNAL_SERVER_ERROR, ""), UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "");

	private PnsError(HttpStatus status, String genericMessage) {
		this.status = status;
		this.genericMessage = genericMessage;
	}

	private HttpStatus status;

	private String genericMessage;

	public final HttpStatus getStatus() {
		return status;
	}

	public final String getGenericMessage() {
		return genericMessage;
	}

}
