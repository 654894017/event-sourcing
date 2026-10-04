package com.damon.eventsourcing.sample.train.aggregate.value_object;

import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.SEAT_PROTECT_TYPE;

import java.util.Map;

public class SeatIndexSelected {

    private Map<Integer, SEAT_PROTECT_TYPE> seatIndexs;

    private Integer weight;

    public SeatIndexSelected(Map<Integer, SEAT_PROTECT_TYPE> seatIndexs, Integer weight) {
        this.seatIndexs = seatIndexs;
        this.weight = weight;
    }

    public Map<Integer, SEAT_PROTECT_TYPE> getSeatIndexs() {
        return seatIndexs;
    }

    public void setSeatIndexs(Map<Integer, SEAT_PROTECT_TYPE> seatIndexs) {
        this.seatIndexs = seatIndexs;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
