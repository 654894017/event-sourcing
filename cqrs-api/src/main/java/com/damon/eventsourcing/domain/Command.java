package com.damon.eventsourcing.domain;

import java.io.Serializable;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Command implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2869549269787386287L;

    private Long aggregateId;

    private Map<String, Object> shardingParams;


    public Command(Long aggregateId, Map<String, Object> shardingParams) {
        checkNotNull(aggregateId);
        this.aggregateId = aggregateId;
        this.shardingParams = shardingParams;
    }

    /**
     * @param aggregateId
     */
    public Command(Long aggregateId) {
        checkNotNull(aggregateId);
        this.aggregateId = aggregateId;
    }

    public Long getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(long aggregateId) {
        this.aggregateId = aggregateId;
    }

    public Map<String, Object> getShardingParams() {
        return shardingParams;
    }

    public void setShardingParams(Map<String, Object> shardingParams) {
        this.shardingParams = shardingParams;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(aggregateId);
    }

}
