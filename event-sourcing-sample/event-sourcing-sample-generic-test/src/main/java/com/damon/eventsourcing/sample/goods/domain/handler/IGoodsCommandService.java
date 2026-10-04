package com.damon.eventsourcing.sample.goods.domain.handler;


import com.damon.eventsourcing.sample.goods.api.GoodsCreateCommand;
import com.damon.eventsourcing.sample.goods.api.GoodsStockCancelDeductionCommand;
import com.damon.eventsourcing.sample.goods.api.GoodsStockCommitDeductionCommand;
import com.damon.eventsourcing.sample.goods.api.GoodsStockTryDeductionCommand;
import com.damon.eventsourcing.sample.goods.domain.aggregate.Goods;

import java.util.concurrent.CompletableFuture;

public interface IGoodsCommandService {

    CompletableFuture<Goods> createGoodsStock(GoodsCreateCommand command);

    CompletableFuture<Integer> tryDeductionStock(GoodsStockTryDeductionCommand command);

    CompletableFuture<Integer> commitDeductionStock(GoodsStockCommitDeductionCommand command);

    CompletableFuture<Integer> cancelDeductionStock(GoodsStockCancelDeductionCommand command);

}
