package com.damon.eventsourcing.sample.goods.api;

import com.damon.eventsourcing.domain.Command;

public class GoodsStockCommitDeductionCommand extends Command {

    /**
     *
     */
    private static final long serialVersionUID = 4371113646204443737L;
    private Long orderId;

    public GoodsStockCommitDeductionCommand(long aggregateId) {
        super(aggregateId);
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
