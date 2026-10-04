package com.damon.eventsourcing.sample.metting.domain.aggregate;

public enum ReserveStatusEnum {

    SUCCEEDED(0, "预定成功"),
    BEOCCUPIED(-1, "区间已被占用");
    private int status;

    private String message;

    ReserveStatusEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }


}
