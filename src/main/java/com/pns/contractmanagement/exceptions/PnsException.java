package com.pns.contractmanagement.exceptions;

import lombok.Getter;

/**
 *
 */
@Getter
public class PnsException extends Exception {
    /** serialVersionUID. */
    private static final long serialVersionUID = -2614013472401988961L;
    private PnsError error;

    /**
     * @param message
     * @param cause
     */
    public PnsException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public PnsException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public PnsException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param error
     */
    public PnsException(PnsError error) {
        super();
        this.error = error;
    }

    /**
     * @param message
     * @param cause
     */
    public PnsException(String message, Throwable cause, PnsError error) {
        super(message, cause);
        this.error = error;
    }

    /**
     * @param message
     */
    public PnsException(String message, PnsError error) {
        super(message);
        this.error = error;
    }

    /**
     * @param cause
     */
    public PnsException(Throwable cause, PnsError error) {
        super(cause);
        this.error = error;
    }

}
