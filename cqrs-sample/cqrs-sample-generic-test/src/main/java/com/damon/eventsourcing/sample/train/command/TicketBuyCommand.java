package com.damon.eventsourcing.sample.train.command;

import com.damon.eventsourcing.domain.Command;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.SEAT_TYPE;

import java.util.List;

public class TicketBuyCommand extends Command {

    private List<Long> userIds;
    private Integer startStationNumber;
    private Integer endStationNumber;
    private SEAT_TYPE seatType;
    /**
     * 选座 0-10 分别对应  n排 ABCDEF  n+1排 ABCDEF
     */
    private List<Integer> seatIndexs;


    /**
     * @param commandId
     * @param aggregateId
     */
    public TicketBuyCommand(long aggregateId) {
        super(aggregateId);
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
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

    public List<Integer> getSeatIndexs() {
        return seatIndexs;
    }

    public void setSeatIndexs(List<Integer> seatIndexs) {
        this.seatIndexs = seatIndexs;
    }
}


