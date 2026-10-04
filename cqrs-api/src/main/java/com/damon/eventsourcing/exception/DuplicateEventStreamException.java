package com.damon.eventsourcing.exception;

public class DuplicateEventStreamException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -8170774802271181073L;

    /**
     *
     */
    public DuplicateEventStreamException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public DuplicateEventStreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public DuplicateEventStreamException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public DuplicateEventStreamException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public DuplicateEventStreamException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
