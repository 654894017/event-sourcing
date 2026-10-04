package com.damon.eventsourcing.sample.train.command;

import com.damon.eventsourcing.domain.Command;

public class TicketCancelCommand extends Command {
    private Long userId;
    private Integer startStationNumber;
    private Integer endStationNumber;

    /**
     * @param aggregateId
     */
    public TicketCancelCommand(long aggregateId) {
        super(aggregateId);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStartStationNumber() {
        return startStationNumber;
    }

    public void setStartStationNumber(Integer startStationNumber) {
        this.startStationNumber = startStationNumber;
    }

    public Integer getEndStationNumber() {
        return endStationNumber;
    }

    public void setEndStationNumber(Integer endStationNumber) {
        this.endStationNumber = endStationNumber;
    }
}
