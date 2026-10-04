package com.damon.eventsourcing.exception;

public class EventApplyException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -6513851101874096469L;

    /**
     *
     */
    public EventApplyException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public EventApplyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public EventApplyException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public EventApplyException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public EventApplyException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }


}
