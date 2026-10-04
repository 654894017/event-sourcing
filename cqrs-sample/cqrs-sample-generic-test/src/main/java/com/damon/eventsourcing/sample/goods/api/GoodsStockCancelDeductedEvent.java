package com.damon.eventsourcing.sample.goods.api;

import com.damon.eventsourcing.domain.Event;

public class GoodsStockCancelDeductedEvent extends Event {

    /**
     *
     */
    private static final long serialVersionUID = -7551341932379515484L;

    private Long orderId;

    public GoodsStockCancelDeductedEvent() {
        super();
    }


    public GoodsStockCancelDeductedEvent(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}