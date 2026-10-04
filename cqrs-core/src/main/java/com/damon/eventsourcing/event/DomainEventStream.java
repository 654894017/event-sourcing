package com.damon.eventsourcing.event;

import com.damon.eventsourcing.domain.Event;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DomainEventStream {
    private Long aggregateId;
    private String aggregateType;
    private Event event;
    private Map<String, Object> shardingParams;

    public Integer getVersion() {
        return event.getVersion();
    }
}
