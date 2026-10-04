package com.damon.eventsourcing.sample.train.event;

import com.damon.eventsourcing.domain.Event;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.SEAT_PROTECT_TYPE;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.SEAT_TYPE;

import java.util.List;
import java.util.Map;

public class TicketBoughtEvent extends Event {
    private List<Long> userIds;
    private Integer startStationNumber;
    private Integer endStationNumber;
    private Map<Integer, SEAT_PROTECT_TYPE> seatIndexs;

    // private Map<Integer, SEAT_PROTECT_TYPE> seatIndex;

    //private SEAT_PROTECT_TYPE seatProtectType;
    /**
     * 用来记录站点到站点间不是严格保护map的key信息
     */
    private Integer s2sSeatRelaxedProtectKey;

    private SEAT_TYPE seatType;

    public TicketBoughtEvent() {

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

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
//
//    public Map<Integer, SEAT_PROTECT_TYPE> getSeatIndexs() {
//        return seatIndexs;
//    }
//
//    public void setSeatIndexs(Map<Integer, SEAT_PROTECT_TYPE> seatIndexs) {
//        this.seatIndexs = seatIndexs;
//    }
//
//    public SEAT_PROTECT_TYPE getSeatProtectType() {
//        return seatProtectType;
//    }
//
//    public void setSeatProtectType(SEAT_PROTECT_TYPE seatProtectType) {
//        this.seatProtectType = seatProtectType;
//    }

    public Integer getS2sSeatRelaxedProtectKey() {
        return s2sSeatRelaxedProtectKey;
    }

    public void setS2sSeatRelaxedProtectKey(Integer s2sSeatRelaxedProtectKey) {
        this.s2sSeatRelaxedProtectKey = s2sSeatRelaxedProtectKey;
    }

    public SEAT_TYPE getSeatType() {
        return seatType;
    }

    public void setSeatType(SEAT_TYPE seatType) {
        this.seatType = seatType;
    }

    public Map<Integer, SEAT_PROTECT_TYPE> getSeatIndexs() {
        return seatIndexs;
    }

    public void setSeatIndexs(Map<Integer, SEAT_PROTECT_TYPE> seatIndexs) {
        this.seatIndexs = seatIndexs;
    }

//    public Map<Integer, SEAT_PROTECT_TYPE> getSeatIndex() {
//        return seatIndex;
//    }
//
//    public void setSeatIndex(Map<Integer, SEAT_PROTECT_TYPE> seatIndex) {
//        this.seatIndex = seatIndex;
//    }
    //    public Map<Long, Map<Integer, SEAT_PROTECT_TYPE>> getSeatIndexMap() {
//        return seatIndexMap;
//    }
//
//    public void setSeatIndexMap(Map<Long, Map<Integer, SEAT_PROTECT_TYPE>> seatIndexMap) {
//        this.seatIndexMap = seatIndexMap;
//    }
}
