package com.damon.eventsourcing.sample.goods.api;

import com.damon.eventsourcing.domain.Command;

public class GoodsStockTryDeductionCommand extends Command {

    /**
     *
     */
    private static final long serialVersionUID = 4371113646204443737L;
    private int number;

    private Long orderId;

    public GoodsStockTryDeductionCommand(long aggregateId) {
        super(aggregateId);
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
