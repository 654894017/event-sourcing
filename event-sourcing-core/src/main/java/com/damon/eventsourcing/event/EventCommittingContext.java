package com.damon.eventsourcing.event;

import com.damon.eventsourcing.domain.AggregateRoot;
import com.damon.eventsourcing.domain.Event;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Data
@Builder
public class EventCommittingContext {

    private Event event;

    private CompletableFuture<Boolean> future;

    private Long aggregateId;

    private String aggregateTypeName;

    private EventCommittingMailBox mailBox;

    private AggregateRoot snapshot;

    private Map<String, Object> shardingParams;

    public Integer getVersion() {
        return event.getVersion();
    }

}
