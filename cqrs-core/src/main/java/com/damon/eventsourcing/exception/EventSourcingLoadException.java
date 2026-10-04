package com.damon.eventsourcing.exception;

public class EventSourcingLoadException extends EventSourcingException {
    public EventSourcingLoadException(String message) {
        super(message);
    }

    public EventSourcingLoadException(Throwable cause) {
        super(cause);
    }

    public EventSourcingLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
