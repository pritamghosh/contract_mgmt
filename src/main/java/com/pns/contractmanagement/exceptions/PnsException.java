package com.pns.contractmanagement.exceptions;

import lombok.Getter;

/**
 *
 */
@Getter
public class PnsException extends RuntimeException {
	/** serialVersionUID. */
	private static final long serialVersionUID = -2614013472401988961L;
	private final PnsError error;

	/**
	 * @param message
	 * @param cause
	 */
	public PnsException(final String message, final Throwable cause) {
		super(message, cause);
		this.error = PnsError.SYSTEM_ERROR;
	}

	/**
	 * @param message
	 */
	public PnsException(final String message) {
		super(message);
		this.error = PnsError.SYSTEM_ERROR;
	}

	/**
	 * @param cause
	 */
	public PnsException(final Throwable cause) {
		super(cause);
		this.error = PnsError.SYSTEM_ERROR;
	}

	/**
	 * @param error
	 */
	public PnsException(final PnsError error) {
		super();
		this.error = error;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PnsException(final String message, final Throwable cause, final PnsError error) {
		super(message, cause);
		this.error = error;
	}

	/**
	 * @param message
	 */
	public PnsException(final String message, final PnsError error) {
		super(message);
		this.error = error;
	}

	/**
	 * @param cause
	 */
	public PnsException(final Throwable cause, final PnsError error) {
		super(cause);
		this.error = error;
	}

}
