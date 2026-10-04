package com.damon.eventsourcing.exception;

public class AggregateProcessingTimeoutException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -6513851101874096469L;

    /**
     *
     */
    public AggregateProcessingTimeoutException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public AggregateProcessingTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public AggregateProcessingTimeoutException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public AggregateProcessingTimeoutException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public AggregateProcessingTimeoutException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }


}
