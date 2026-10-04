package com.damon.eventsourcing.exception;

public class AggregateEventConflictException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -6513851101874096469L;
    private long aggregateId;
    private String aggregateType;

    /**
     *
     */
    public AggregateEventConflictException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public AggregateEventConflictException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public AggregateEventConflictException(long aggregateId, String aggregateType, Throwable cause) {
        // TODO Auto-generated constructor stub
        super(cause);
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
    }

    public long getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(long aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }


}
