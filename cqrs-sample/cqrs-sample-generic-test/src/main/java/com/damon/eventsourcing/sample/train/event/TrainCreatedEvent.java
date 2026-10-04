package com.damon.eventsourcing.sample.train.event;


import com.damon.eventsourcing.domain.Event;
import com.damon.eventsourcing.sample.train.aggregate.value_object.TrainCarriage;

import java.util.List;

public class TrainCreatedEvent extends Event {

    private List<Integer> station2StationBusinessList;
    private int businessSeatCount;
    private List<Integer> station2StationFirstList;
    private int firstSeatCount;
    private List<Integer> station2StationSecondList;
    private int secondSeatCount;
    private List<Integer> station2StationStandingList;
    private int standingCount;

    private List<TrainCarriage> businessTrainCarriageList;
    private List<TrainCarriage> firstTrainCarriageList;
    private List<TrainCarriage> secondTrainCarriageList;
    private List<TrainCarriage> standingTrainCarriageList;


    public TrainCreatedEvent() {

    }

    public List<Integer> getStation2StationBusinessList() {
        return station2StationBusinessList;
    }

    public void setStation2StationBusinessList(List<Integer> station2StationBusinessList) {
        this.station2StationBusinessList = station2StationBusinessList;
    }

    public int getBusinessSeatCount() {
        return businessSeatCount;
    }

    public void setBusinessSeatCount(int businessSeatCount) {
        this.businessSeatCount = businessSeatCount;
    }

    public List<Integer> getStation2StationFirstList() {
        return station2StationFirstList;
    }

    public void setStation2StationFirstList(List<Integer> station2StationFirstList) {
        this.station2StationFirstList = station2StationFirstList;
    }

    public int getFirstSeatCount() {
        return firstSeatCount;
    }

    public void setFirstSeatCount(int firstSeatCount) {
        this.firstSeatCount = firstSeatCount;
    }

    public List<Integer> getStation2StationSecondList() {
        return station2StationSecondList;
    }

    public void setStation2StationSecondList(List<Integer> station2StationSecondList) {
        this.station2StationSecondList = station2StationSecondList;
    }

    public int getSecondSeatCount() {
        return secondSeatCount;
    }

    public void setSecondSeatCount(int secondSeatCount) {
        this.secondSeatCount = secondSeatCount;
    }

    public List<Integer> getStation2StationStandingList() {
        return station2StationStandingList;
    }

    public void setStation2StationStandingList(List<Integer> station2StationStandingList) {
        this.station2StationStandingList = station2StationStandingList;
    }

    public int getStandingCount() {
        return standingCount;
    }

    public void setStandingCount(int standingCount) {
        this.standingCount = standingCount;
    }

    public List<TrainCarriage> getBusinessTrainCarriageList() {
        return businessTrainCarriageList;
    }

    public void setBusinessTrainCarriageList(List<TrainCarriage> businessTrainCarriageList) {
        this.businessTrainCarriageList = businessTrainCarriageList;
    }

    public List<TrainCarriage> getFirstTrainCarriageList() {
        return firstTrainCarriageList;
    }

    public void setFirstTrainCarriageList(List<TrainCarriage> firstTrainCarriageList) {
        this.firstTrainCarriageList = firstTrainCarriageList;
    }

    public List<TrainCarriage> getSecondTrainCarriageList() {
        return secondTrainCarriageList;
    }

    public void setSecondTrainCarriageList(List<TrainCarriage> secondTrainCarriageList) {
        this.secondTrainCarriageList = secondTrainCarriageList;
    }

    public List<TrainCarriage> getStandingTrainCarriageList() {
        return standingTrainCarriageList;
    }

    public void setStandingTrainCarriageList(List<TrainCarriage> standingTrainCarriageList) {
        this.standingTrainCarriageList = standingTrainCarriageList;
    }
}

