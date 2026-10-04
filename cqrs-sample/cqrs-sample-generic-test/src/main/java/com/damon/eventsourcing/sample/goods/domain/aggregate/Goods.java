package com.damon.eventsourcing.sample.goods.domain.aggregate;

import com.damon.eventsourcing.domain.AggregateRoot;
import com.damon.eventsourcing.sample.goods.api.GoodsCreatedEvent;
import com.damon.eventsourcing.sample.goods.api.GoodsStockCancelDeductedEvent;
import com.damon.eventsourcing.sample.goods.api.GoodsStockCommitDeductedEvent;
import com.damon.eventsourcing.sample.goods.api.GoodsStockTryDeductedEvent;

import java.util.HashMap;
import java.util.Map;

public class Goods extends AggregateRoot {

    /**
     *
     */
    private static final long serialVersionUID = -7591043196387906498L;

    private int number;

    private String name;

    private Map<Long, Integer> orderStockMap;

    private Long id;


    public Goods(long id, String name, int number) {
        super(id);
        applyNewEvent(new GoodsCreatedEvent(id, name, number));
    }

    public int tryDeductionStock(Long orderId, int deductionNumber) {
        if (this.number - deductionNumber < 0) {
            //库存不足
            return -1;
        }
        applyNewEvent(new GoodsStockTryDeductedEvent(orderId, deductionNumber));
        return 1;
    }

    public int commitDeductionStock(Long orderId) {
        if (orderStockMap.get(orderId) == null) {
            //不存在扣减记录
            return -1;
        }
        applyNewEvent(new GoodsStockCommitDeductedEvent(orderId));
        //成功
        return 1;
    }

    public int cancelDeductionStock(Long orderId) {
        if (orderStockMap.get(orderId) == null) {
            //不存在扣减记录
            return -1;
        }
        applyNewEvent(new GoodsStockCancelDeductedEvent(orderId));
        //成功
        return 1;
    }

    @SuppressWarnings("unused")
    private void apply(GoodsStockTryDeductedEvent event) {
        number -= event.getNumber();
        orderStockMap.put(event.getOrderId(), event.getNumber());
    }

    private void apply(GoodsStockCommitDeductedEvent event) {
        orderStockMap.remove(event.getOrderId());
    }

    private void apply(GoodsStockCancelDeductedEvent event) {
        int dedcutedNumber = orderStockMap.get(event.getOrderId());
        this.number += dedcutedNumber;
        orderStockMap.remove(event.getOrderId());
    }

    @SuppressWarnings("unused")
    private void apply(GoodsCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.number = event.getNumber();
        this.orderStockMap = new HashMap<>();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
