package com.damon.eventsourcing.exception;

public class EventSourcingException extends RuntimeException {
    public EventSourcingException(String message) {
        super(message);
    }

    public EventSourcingException(Throwable cause) {
        super(cause);
    }

    public EventSourcingException(String message, Throwable cause) {
        super(message, cause);
    }
}
