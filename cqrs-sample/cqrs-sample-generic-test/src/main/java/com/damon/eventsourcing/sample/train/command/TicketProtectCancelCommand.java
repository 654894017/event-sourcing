package com.damon.eventsourcing.sample.train.command;

import com.damon.eventsourcing.domain.Command;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.SEAT_TYPE;

public class TicketProtectCancelCommand extends Command {
    private Integer startStationNumber;
    private Integer endStationNumber;
    private Boolean strict;
    private SEAT_TYPE seatType;

    /**
     * @param aggregateId
     */
    public TicketProtectCancelCommand(long aggregateId) {
        super(aggregateId);
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

    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    public SEAT_TYPE getSeatType() {
        return seatType;
    }

    public void setSeatType(SEAT_TYPE seatType) {
        this.seatType = seatType;
    }
}
