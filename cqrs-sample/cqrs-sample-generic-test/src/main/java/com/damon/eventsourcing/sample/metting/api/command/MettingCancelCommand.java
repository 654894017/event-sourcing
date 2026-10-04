package com.damon.eventsourcing.sample.metting.api.command;

import com.damon.eventsourcing.domain.Command;

public class MettingCancelCommand extends Command {

    private String reserveFlag;

    private Long userId;

    public MettingCancelCommand(Long aggregateId, String reserveFlag, Long userId) {
        super(aggregateId);
        this.reserveFlag = reserveFlag;
        this.userId = userId;
    }

    public String getReserveFlag() {
        return reserveFlag;
    }


    public Long getUserId() {
        return userId;
    }

}
