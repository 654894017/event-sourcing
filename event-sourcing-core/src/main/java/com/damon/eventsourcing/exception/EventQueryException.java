package com.damon.eventsourcing.exception;

public class EventQueryException extends RuntimeException {
    public EventQueryException(String message) {
        super(message);
    }

    public EventQueryException(Throwable cause) {
        super(cause);
    }
}
