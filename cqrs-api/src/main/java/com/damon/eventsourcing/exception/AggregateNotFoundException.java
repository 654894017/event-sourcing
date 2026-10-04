package com.damon.eventsourcing.exception;

import static java.lang.String.format;

public class AggregateNotFoundException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1621938828088924510L;

    public AggregateNotFoundException(long id) {
        super(format("Aggregate with id '%s' could not be found", id));
    }
}
