package com.damon.eventsourcing.exception;

public class EventSendException extends RuntimeException {
    public EventSendException(String message) {
        super(message);
    }

    public EventSendException(Throwable cause) {
        super(cause);
    }

    public EventSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
