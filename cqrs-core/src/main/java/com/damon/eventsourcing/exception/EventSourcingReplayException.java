package com.damon.eventsourcing.exception;

public class EventSourcingReplayException extends EventSourcingException {
    public EventSourcingReplayException(String message) {
        super(message);
    }

    public EventSourcingReplayException(Throwable cause) {
        super(cause);
    }

    public EventSourcingReplayException(String message, Throwable cause) {
        super(message, cause);
    }
}
