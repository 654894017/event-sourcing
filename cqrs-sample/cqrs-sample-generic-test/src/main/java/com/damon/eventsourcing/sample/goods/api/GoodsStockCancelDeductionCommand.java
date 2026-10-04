package com.damon.eventsourcing.sample.goods.api;

import com.damon.eventsourcing.domain.Command;

public class GoodsStockCancelDeductionCommand extends Command {

    /**
     *
     */
    private static final long serialVersionUID = 4371113646204443737L;

    private Long orderId;

    public GoodsStockCancelDeductionCommand(long aggregateId) {
        super(aggregateId);
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
