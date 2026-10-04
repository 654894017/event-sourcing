package com.damon.eventsourcing.cache;

import com.damon.eventsourcing.domain.AggregateRoot;

public interface IAggregateCache {

    void update(long id, AggregateRoot aggregate);

    <T extends AggregateRoot> T get(long id);

}
