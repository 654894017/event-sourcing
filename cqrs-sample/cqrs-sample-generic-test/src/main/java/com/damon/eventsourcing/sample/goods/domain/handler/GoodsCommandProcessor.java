package com.damon.eventsourcing.sample.goods.domain.handler;


import com.damon.eventsourcing.command.CommandProcessor;
import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.sample.goods.api.GoodsCreateCommand;
import com.damon.eventsourcing.sample.goods.api.GoodsStockCancelDeductionCommand;
import com.damon.eventsourcing.sample.goods.api.GoodsStockCommitDeductionCommand;
import com.damon.eventsourcing.sample.goods.api.GoodsStockTryDeductionCommand;
import com.damon.eventsourcing.sample.goods.domain.aggregate.Goods;

import java.util.concurrent.CompletableFuture;

public class GoodsCommandProcessor extends CommandProcessor<Goods> implements IGoodsCommandService {

    public GoodsCommandProcessor(EventSourcingConfig eventSourcingConfig) {
        super(eventSourcingConfig);
    }

    @Override
    public CompletableFuture<Goods> createGoodsStock(GoodsCreateCommand command) {
        return super.process(command, () -> new Goods(command.getAggregateId(), command.getName(), command.getNumber()));
    }

    @Override
    public CompletableFuture<Integer> tryDeductionStock(GoodsStockTryDeductionCommand command) {
        return super.process(command, goods -> goods.tryDeductionStock(command.getOrderId(), command.getNumber()));
    }

    @Override
    public CompletableFuture<Integer> commitDeductionStock(GoodsStockCommitDeductionCommand command) {
        return super.process(command, goods -> goods.commitDeductionStock(command.getOrderId()));
    }

    @Override
    public CompletableFuture<Integer> cancelDeductionStock(GoodsStockCancelDeductionCommand command) {
        return super.process(command, goods -> goods.cancelDeductionStock(command.getOrderId()));
    }


    @Override
    public Goods getAggregateSnapshot(long aggregateId, Class<Goods> classes) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> saveAggregateSnapshot(Goods goods) {
        System.out.println(goods.getId() + ":" + goods.getNumber() + ":" + goods.getName() + ":" + goods.getVersion());
        return CompletableFuture.completedFuture(true);
    }

//    @Override
//    public Goods createAggregateSnapshot(Goods aggregate) {
//        Goods snap = new Goods();
//        snap.setName(aggregate.getName());
//        snap.setNumber(aggregate.getNumber());
//        snap.setId(aggregate.getId());
//        snap.setVersion(aggregate.getVersion());
//        return snap;
//    }
//
//    @Override
//    public long snapshotCycle() {
//        return 5;
//    }


}
