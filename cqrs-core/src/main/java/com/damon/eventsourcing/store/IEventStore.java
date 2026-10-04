package com.damon.eventsourcing.store;

import com.damon.eventsourcing.domain.AggregateRoot;
import com.damon.eventsourcing.domain.Event;
import com.damon.eventsourcing.event.AggregateEventAppendResult;
import com.damon.eventsourcing.event.DomainEventStream;
import com.damon.eventsourcing.event.EventSendingContext;

import java.util.List;
import java.util.Map;

public interface IEventStore {

    AggregateEventAppendResult store(List<DomainEventStream> streams);

    List<Event> load(long aggregateId, Class<? extends AggregateRoot> aggregateClass, int startVersion, int endVersion, Map<String, Object> shardingParams);

    List<EventSendingContext> queryWaitingSendEvents(String dataSourceName, String tableName, long offsetId);
}
