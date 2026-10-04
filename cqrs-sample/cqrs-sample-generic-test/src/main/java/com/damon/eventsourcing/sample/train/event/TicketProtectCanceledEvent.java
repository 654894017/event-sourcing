package com.damon.eventsourcing.sample.train.event;

import com.damon.eventsourcing.domain.Event;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.SEAT_TYPE;

public class TicketProtectCanceledEvent extends Event {
    private Integer startStationNumber;
    private Integer endStationNumber;

    private SEAT_TYPE seatType;

    public TicketProtectCanceledEvent() {
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

    public SEAT_TYPE getSeatType() {
        return seatType;
    }

    public void setSeatType(SEAT_TYPE seatType) {
        this.seatType = seatType;
    }
}
