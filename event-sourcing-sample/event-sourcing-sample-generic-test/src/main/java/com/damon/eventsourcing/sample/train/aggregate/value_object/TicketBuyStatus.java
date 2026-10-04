package com.damon.eventsourcing.sample.train.aggregate.value_object;

import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.SEAT_PROTECT_TYPE;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.TICKET_BUY_STATUS;

import java.util.List;
import java.util.Map;

public class TicketBuyStatus {

    private final TICKET_BUY_STATUS stauts;

    private List<Long> userIds;

    private Map<Integer, SEAT_PROTECT_TYPE> seatIndexs;

    public TicketBuyStatus(TICKET_BUY_STATUS stauts) {
        this.stauts = stauts;
    }

    public TicketBuyStatus(TICKET_BUY_STATUS stauts, List<Long> userIds, Map<Integer, SEAT_PROTECT_TYPE> seatIndexs) {
        this.stauts = stauts;
        this.userIds = userIds;
        this.seatIndexs = seatIndexs;
    }

    public TICKET_BUY_STATUS getStauts() {
        return stauts;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }


    public Map<Integer, SEAT_PROTECT_TYPE> getSeatIndexs() {
        return seatIndexs;
    }

    public void setSeatIndexs(Map<Integer, SEAT_PROTECT_TYPE> seatIndexs) {
        this.seatIndexs = seatIndexs;
    }
}
