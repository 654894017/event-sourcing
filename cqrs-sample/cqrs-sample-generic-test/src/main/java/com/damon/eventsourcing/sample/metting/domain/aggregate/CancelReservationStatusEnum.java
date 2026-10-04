package com.damon.eventsourcing.sample.metting.domain.aggregate;

public enum CancelReservationStatusEnum {

    SUCCEEDED(0, "预定成功"),
    NONEXISTENT(-1, "不存在预定信息"),
    UNMACHED(-2, "预定信息不匹配");
    private int status;

    private String message;

    CancelReservationStatusEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }


}
