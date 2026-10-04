package com.damon.eventsourcing;

import com.damon.eventsourcing.command.ICommandProcessor;
import com.damon.eventsourcing.domain.AggregateRoot;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class EventSourcingContext {

    private static Map<String, ICommandProcessor<?>> map = new HashMap<>();

    public static synchronized <T extends AggregateRoot> void add(String aggregateType, ICommandProcessor<T> service) {
        map.put(aggregateType, service);
    }

    public static <T extends AggregateRoot> ICommandProcessor<T> get(String aggregateType) {
        return (ICommandProcessor<T>) map.get(aggregateType);
    }
}
