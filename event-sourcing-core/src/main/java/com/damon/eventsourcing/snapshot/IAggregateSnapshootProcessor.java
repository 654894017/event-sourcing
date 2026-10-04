package com.damon.eventsourcing.snapshot;

import com.damon.eventsourcing.domain.AggregateRoot;

public interface IAggregateSnapshootProcessor {

    void saveAggregateSnapshot(AggregateRoot aggregate);

}
