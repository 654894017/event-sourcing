package com.damon.eventsourcing.sample.train.command;

import com.damon.eventsourcing.domain.Command;

public class TrainStockGetCommand extends Command {
    /**
     * @param commandId
     * @param aggregateId
     */
    public TrainStockGetCommand(long commandId, long aggregateId) {
        super(aggregateId);
    }

}
