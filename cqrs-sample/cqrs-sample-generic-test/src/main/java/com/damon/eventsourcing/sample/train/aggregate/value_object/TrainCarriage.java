package com.damon.eventsourcing.sample.train.aggregate.value_object;

import com.damon.eventsourcing.domain.ValueObject;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.SEAT_TYPE;

/**
 * 火车车厢
 */
//@Builder
public class TrainCarriage implements ValueObject {

    /**
     * 车厢编号
     */
    private Integer number;
    /**
     * 车站座位开始编号
     */
    private Integer startNumber;
    /**
     * 车站座位结束编号
     */
    private Integer endNumber;
    /**
     * 车厢类型   0 特等座   1 一等做  2 二等座
     */
    private SEAT_TYPE seatType;

    //  private Integer number;
    public TrainCarriage(Integer number, Integer startNumber, Integer endNumber, SEAT_TYPE seatType) {
        this.number = number;
        this.startNumber = startNumber;
        this.endNumber = endNumber;
        this.seatType = seatType;
    }

    public TrainCarriage() {
    }

    public Integer getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(Integer startNumber) {
        this.startNumber = startNumber;
    }

    public Integer getEndNumber() {
        return endNumber;
    }

    public void setEndNumber(Integer endNumber) {
        this.endNumber = endNumber;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public SEAT_TYPE getSeatType() {
        return seatType;
    }

    public void setSeatType(SEAT_TYPE seatType) {
        this.seatType = seatType;
    }
}
