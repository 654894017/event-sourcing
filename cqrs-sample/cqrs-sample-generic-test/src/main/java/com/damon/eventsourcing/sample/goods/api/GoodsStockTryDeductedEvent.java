package com.damon.eventsourcing.sample.goods.api;

import com.damon.eventsourcing.domain.Event;

public class GoodsStockTryDeductedEvent extends Event {

    /**
     *
     */
    private static final long serialVersionUID = -7551341932379515484L;

    private Long orderId;
    private int number;

    public GoodsStockTryDeductedEvent() {
        super();
    }


    /**
     * @param number
     */
    public GoodsStockTryDeductedEvent(Long orderId, int number) {
        this.number = number;
        this.orderId = orderId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}