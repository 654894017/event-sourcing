package com.damon.eventsourcing.sample.goods;

import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.sample.TestConfig;
import com.damon.eventsourcing.sample.goods.api.GoodsCreateCommand;
import com.damon.eventsourcing.sample.goods.api.GoodsStockTryDeductionCommand;
import com.damon.eventsourcing.sample.goods.domain.aggregate.Goods;
import com.damon.eventsourcing.sample.goods.domain.handler.GoodsCommandProcessor;

import java.util.*;
import java.util.concurrent.*;

public class GoodsApplication {

    private static final int runTotalCount = 4 * 2000 * 1000;

    private static final int goodsCount = 4;

    private static final int threadNumber = 400;

    private static final ExecutorService service = Executors.newFixedThreadPool(threadNumber);

    private static final int exeCount = 100000;

    public static void main(String[] args) throws Exception {
        EventSourcingConfig eventSourcingConfig = TestConfig.init();
        GoodsCommandProcessor handler = new GoodsCommandProcessor(eventSourcingConfig);
        List<Long> goodsIds = initGoods(handler);
        int size = goodsIds.size();
        CountDownLatch latch = new CountDownLatch(runTotalCount);
        long from = new Date().getTime();
        System.out.println("start");
        for (int i = 0; i < threadNumber; i++) {
            service.submit(() -> {
                for (int count = 0; count < exeCount; count++) {
                    int index = ThreadLocalRandom.current().nextInt(size);
                    CompletableFuture<Integer> future = handler.tryDeductionStock(new GoodsStockTryDeductionCommand(goodsIds.get(index)));
                    try {
                        future.join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        latch.await();
        long time = calculateTimeConsumption(from, new Date().getTime());
        long tps = runTotalCount / (time / 1000);
        System.out.println("tps:" + tps);
    }

    private static List<Long> initGoods(GoodsCommandProcessor handler) {
        List<Long> ids = new ArrayList<>();
        for (int i = 1; i <= goodsCount; i++) {
            Map<String, Object> shardingParms = new HashMap<>();
            shardingParms.put("a1", "a" + i);
            GoodsCreateCommand command1 = new GoodsCreateCommand(i, "iphone 6 plus " + i, 1000);
            System.out.println(handler.process(command1, () -> new Goods(command1.getAggregateId(), command1.getName(), command1.getNumber())).join());
            ids.add((long) (i));
        }
        return ids;
    }

    private static long calculateTimeConsumption(long from, long to) {
        return to - from;
    }

}
