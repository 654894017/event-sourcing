package com.damon.eventsourcing.sample.metting.api.command;

import com.damon.eventsourcing.domain.Command;

public class MettingGetCommand extends Command {

    public MettingGetCommand(Long aggregateId) {
        super(aggregateId);
    }
}
