package com.damon.eventsourcing.event;

import com.damon.eventsourcing.domain.AggregateRoot;
import com.damon.eventsourcing.recovery.AggregateRecoveryProcessor;
import com.damon.eventsourcing.store.IEventStore;
import com.damon.eventsourcing.utils.NamedThreadFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 领域事件提交服务
 *
 * @author xianping_lu
 */
public class EventCommittingService {

    private final List<EventCommittingMailBox> eventCommittingMailBoxs;

    private final ExecutorService eventCommittingService;

    private final ExecutorService aggregateRecoverService;

    private final IEventStore eventStore;

    private final int mailboxNumber;

    private final AggregateRecoveryProcessor aggregateRecoveryProcessor;

    /**
     * @param eventStore
     * @param mailBoxNumber              不建议设置过大的数值（会导致磁盘顺序写，变成随机写模式，性能下降）
     * @param eventBatchStoreSize        事件批量提交的大小，如果event store是机械硬盘可以加大此大小。
     * @param recoverCorePoolSize        聚合事件冲突恢复最大线程数
     * @param aggregateRecoveryProcessor
     */
    public EventCommittingService(IEventStore eventStore,
                                  int mailBoxNumber,
                                  int eventBatchStoreSize,
                                  int recoverCorePoolSize,
                                  AggregateRecoveryProcessor aggregateRecoveryProcessor
    ) {
        this.eventCommittingMailBoxs = new ArrayList<>(mailBoxNumber);
        this.eventCommittingService = Executors.newFixedThreadPool(mailBoxNumber, new NamedThreadFactory("event-committing-pool"));
        this.aggregateRecoverService = Executors.newFixedThreadPool(recoverCorePoolSize, new NamedThreadFactory("aggregate-recover-pool"));
        this.mailboxNumber = mailBoxNumber;
        this.eventStore = eventStore;
        this.aggregateRecoveryProcessor = aggregateRecoveryProcessor;
        for (int number = 0; number < mailBoxNumber; number++) {
            EventCommittingMailBox mailBox = new EventCommittingMailBox(
                    eventCommittingService, this::batchStoreEvent, number, eventBatchStoreSize
            );
            eventCommittingMailBoxs.add(mailBox);
        }
    }

    /**
     * 提交聚合事件
     *
     * @param context
     */
    public void commitDomainEventAsync(EventCommittingContext context) {
        EventCommittingMailBox maxibox = getEventCommittingMailBox(context.getAggregateId());
        maxibox.enqueue(context);
    }

    private EventCommittingMailBox getEventCommittingMailBox(Long aggregateId) {
        int hash = aggregateId.hashCode();
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        int index = hash % mailboxNumber;
        return eventCommittingMailBoxs.get(index);
    }

    /**
     * 批量保存聚合事件
     *
     * @param contexts
     */
    private <T extends AggregateRoot> void batchStoreEvent(List<EventCommittingContext> contexts) {
        Map<Long, Map<String, Object>> shardingParamsMap = new HashMap<>();
        Map<Long, List<EventCommittingContext>> aggregateEventMap = new HashMap<>();
        List<DomainEventStream> eventStream = contexts.stream().map(context -> {
            shardingParamsMap.putIfAbsent(context.getAggregateId(), context.getShardingParams());
            aggregateEventMap.computeIfAbsent(context.getAggregateId(), aggregateId -> new ArrayList<>()).add(context);
            return DomainEventStream.builder()
                    .event(context.getEvent())
                    .shardingParams(context.getShardingParams())
                    .aggregateId(context.getAggregateId())
                    .aggregateType(context.getAggregateTypeName())
                    .build();
        }).collect(Collectors.toList());
        AggregateEventAppendResult results = eventStore.store(eventStream);
        // 1.存储成功
        results.getSucceedResults().forEach(result -> {
            aggregateEventMap.get(result.getAggregateId())
                    .forEach(context -> context.getFuture().complete(true));
        });
        // 2.冲突的聚合event
        results.getDuplicateEventResults().forEach(result -> {
            asyncRecoveryAggregate(
                    shardingParamsMap, result.getAggreateId(), result.getAggregateType(), aggregateEventMap, result.getThrowable()
            ).join();
        });
        // 3.异常的聚合
        results.getExceptionResults().forEach(result -> {
            asyncRecoveryAggregate(
                    shardingParamsMap, result.getAggreateId(), result.getAggregateType(), aggregateEventMap, result.getThrowable()
            ).join();
        });
    }

    private CompletableFuture<Void> asyncRecoveryAggregate(
            Map<Long, Map<String, Object>> shardingParamsMap,
            Long aggreateId,
            String aggregateType,
            Map<Long, List<EventCommittingContext>> aggregateEventMap,
            Throwable e
    ) {
        return CompletableFuture.runAsync(() -> {
            try {
                aggregateRecoveryProcessor.recover(aggreateId, aggregateType, shardingParamsMap.get(aggreateId));
            } finally {
                List<EventCommittingContext> contexts = aggregateEventMap.remove(aggreateId);
                for (EventCommittingContext context : contexts) {
                    context.getFuture().completeExceptionally(e);
                }
                removeAggregateEvent(aggreateId, e);
            }
        }, aggregateRecoverService);
    }

    private void removeAggregateEvent(Long aggregateId, Throwable e) {
        EventCommittingMailBox mailbox = getEventCommittingMailBox(aggregateId);
        ConcurrentHashMap<String, EventCommittingContext> aggregateContextMap = mailbox.removeAggregateAllEventCommittingContexts(aggregateId);
        aggregateContextMap.forEach((id, context) ->
                context.getFuture().completeExceptionally(e)
        );
    }
}
