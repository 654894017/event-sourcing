package com.damon.eventsourcing.sample.metting.domain.aggregate;

public class ReseveStatus {
    private ReserveStatusEnum reserveStatusEnum;
    private String reserveFlag;

    public ReseveStatus(ReserveStatusEnum reserveStatusEnum, String reserveFlag) {
        this.reserveStatusEnum = reserveStatusEnum;
        this.reserveFlag = reserveFlag;
    }

    public ReseveStatus(ReserveStatusEnum reserveStatusEnum) {
        this.reserveStatusEnum = reserveStatusEnum;
    }

    public ReserveStatusEnum getReserveStatusEnum() {
        return reserveStatusEnum;
    }

    public String getReserveFlag() {
        return reserveFlag;
    }

}
