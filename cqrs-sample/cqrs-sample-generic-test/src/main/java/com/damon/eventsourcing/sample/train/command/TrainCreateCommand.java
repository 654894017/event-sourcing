package com.damon.eventsourcing.sample.train.command;


import com.damon.eventsourcing.domain.Command;
import com.damon.eventsourcing.sample.train.aggregate.value_object.TrainCarriage;

import java.util.List;

public class TrainCreateCommand extends Command {

    private List<Integer> station2StationBusinessList;
    private List<Integer> station2StationFirstList;
    private List<Integer> station2StationSecondList;
    private List<Integer> station2StationStandingList;

    private List<TrainCarriage> businessTrainCarriageList;
    private List<TrainCarriage> firstTrainCarriageList;
    private List<TrainCarriage> secondTrainCarriageList;
    private List<TrainCarriage> standingTrainCarriageList;


    /**
     * @param aggregateId
     */
    public TrainCreateCommand(long aggregateId) {
        super(aggregateId);
    }

    public List<Integer> getStation2StationBusinessList() {
        return station2StationBusinessList;
    }

    public void setStation2StationBusinessList(List<Integer> station2StationBusinessList) {
        this.station2StationBusinessList = station2StationBusinessList;
    }

    public List<Integer> getStation2StationFirstList() {
        return station2StationFirstList;
    }

    public void setStation2StationFirstList(List<Integer> station2StationFirstList) {
        this.station2StationFirstList = station2StationFirstList;
    }

    public List<Integer> getStation2StationSecondList() {
        return station2StationSecondList;
    }

    public void setStation2StationSecondList(List<Integer> station2StationSecondList) {
        this.station2StationSecondList = station2StationSecondList;
    }

    public List<Integer> getStation2StationStandingList() {
        return station2StationStandingList;
    }

    public void setStation2StationStandingList(List<Integer> station2StationStandingList) {
        this.station2StationStandingList = station2StationStandingList;
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

