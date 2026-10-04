package com.damon.eventsourcing.sample.train.dto;

import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.SEAT_TYPE;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class TrainStockDTO {

    private Map<SEAT_TYPE, ConcurrentSkipListMap<Integer, Integer>> s2sSeatCountMap;

    private Long id;

    public Map<SEAT_TYPE, ConcurrentSkipListMap<Integer, Integer>> getS2sSeatCountMap() {
        return s2sSeatCountMap;
    }

    public void setS2sSeatCountMap(Map<SEAT_TYPE, ConcurrentSkipListMap<Integer, Integer>> s2sSeatCountMap) {
        this.s2sSeatCountMap = s2sSeatCountMap;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
