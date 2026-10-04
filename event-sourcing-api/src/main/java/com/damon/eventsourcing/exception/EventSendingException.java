package com.damon.eventsourcing.exception;

public class EventSendingException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -6513851101874096469L;

    /**
     *
     */
    public EventSendingException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public EventSendingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public EventSendingException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public EventSendingException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public EventSendingException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }


}
