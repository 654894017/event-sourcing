package com.damon.eventsourcing.command;

import cn.hutool.core.collection.CollUtil;
import com.damon.eventsourcing.EventSourcingContext;
import com.damon.eventsourcing.cache.IAggregateCache;
import com.damon.eventsourcing.config.AggregateSlotLock;
import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.domain.AggregateRoot;
import com.damon.eventsourcing.domain.Command;
import com.damon.eventsourcing.domain.Event;
import com.damon.eventsourcing.event.EventCommittingContext;
import com.damon.eventsourcing.event.EventCommittingService;
import com.damon.eventsourcing.exception.AggregateEventConflictException;
import com.damon.eventsourcing.exception.AggregateNotFoundException;
import com.damon.eventsourcing.exception.AggregateProcessingTimeoutException;
import com.damon.eventsourcing.exception.EventStoreException;
import com.damon.eventsourcing.snapshot.IAggregateSnapshootProcessor;
import com.damon.eventsourcing.store.IEventStore;
import com.damon.eventsourcing.utils.GenericsUtils;
import com.damon.eventsourcing.utils.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 聚合领域服务抽象类
 * <p>
 * 可以在此服务上封装dubbo、spring cloud 微服务框架。
 * <p>
 * 注意：负载均衡需要采用hash机制，建议使用一致性hash，当集群扩容、缩容时对聚合根的恢复影响较小。
 *
 * @author xianping_lu
 */
@Slf4j
public abstract class CommandProcessor<T extends AggregateRoot> implements ICommandProcessor<T> {
    /**
     * 聚合回溯等待超时时间
     */
    private final Long LOCK_WAITTING_TIME = 5000L;
    private final EventCommittingService eventCommittingService;
    private final IAggregateCache aggregateCache;
    private final IEventStore eventStore;
    private final IAggregateSnapshootProcessor aggregateSnapshootService;
    private final AggregateSlotLock aggregateSlotLock;

    public CommandProcessor(EventSourcingConfig eventSourcingConfig) {
        this.eventCommittingService = eventSourcingConfig.getEventCommittingService();
        this.aggregateCache = eventSourcingConfig.getAggregateCache();
        this.eventStore = eventSourcingConfig.getEventStore();
        this.aggregateSnapshootService = eventSourcingConfig.getAggregateSnapshootService();
        this.aggregateSlotLock = eventSourcingConfig.getAggregateSlotLock();
        EventSourcingContext.add(getAggregateType().getTypeName(), this);
    }

    @SuppressWarnings("unchecked")
    private Class<T> getAggregateType() {
        return GenericsUtils.getSuperClassGenricType(this.getClass(), 0);
    }

    private T load(final long aggregateId, final Class<T> aggregateType, Map<String, Object> shardingParams) {
        T aggregate = aggregateCache.get(aggregateId);
        if (aggregate != null) {
            log.debug("aggregate id: {}, aggreage type : {} from load local cache ", aggregateId, aggregate.getClass().getTypeName());
            return aggregate;
        }
        T snapshot = this.getAggregateSnapshot(aggregateId, aggregateType);
        if (snapshot == null) {
            snapshot = ReflectUtils.newInstance(aggregateType);
            snapshot.setId(aggregateId);
        }
        int startVersion = snapshot.getVersion() + 1;
        long loadStartTime = System.currentTimeMillis();
        List<Event> events = eventStore.load(
                aggregateId, aggregateType, startVersion, Integer.MAX_VALUE, shardingParams
        );
        if (CollUtil.isEmpty(events)) {
            throw new AggregateNotFoundException(aggregateId);
        }
        log.info("aggregate id: {} , type: {}, start version : {}, end version : {}, load costTime : {}",
                aggregateId, aggregateType, startVersion, Integer.MAX_VALUE, System.currentTimeMillis() - loadStartTime);
        long replayStartTime = System.currentTimeMillis();

        snapshot.replayEvents(events);
        log.info("aggregate id: {} , type: {}, start version : {}, end version : {}, replay costTime : {}",
                aggregateId, aggregateType, startVersion, Integer.MAX_VALUE, System.currentTimeMillis() - replayStartTime);
        aggregateCache.update(aggregateId, snapshot);
        log.info("aggregate id: {} , type: {} , event sourcing succeed. start version : {}, end version : {}",
                aggregateId, aggregateType, startVersion, Integer.MAX_VALUE
        );
        return snapshot;
    }

    /**
     * 聚合根初始化处理
     *
     * @param command
     * @param supplier
     * @param lockWaitingTime 聚合根更新冲突时间，会暂停当前聚合根新的command的处理，直到聚合根恢复完成时才接受新的command。
     * @throws AggregateEventConflictException 出现此异常的原因是当前聚合根在多个实例中存在（集群扩容时），可以捕获此异常然后重新在client发起调用，当前的请求会负载到新的实例上。
     * @throws EventStoreException             持久化事件时出现预料之外的错误。
     */
    @Override
    public CompletableFuture<T> process(final Command command, final Supplier<T> supplier, Long lockWaitingTime) {
        checkNotNull(supplier);
        checkNotNull(command);
        checkNotNull(command.getAggregateId());
        ReentrantLock lock = aggregateSlotLock.getLock(command.getAggregateId());
        boolean flag;
        try {
            flag = lock.tryLock(lockWaitingTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            CompletableFuture<T> exceptionFuture = new CompletableFuture<>();
            exceptionFuture.completeExceptionally(e);
            return exceptionFuture;
        }
        if (!flag) {
            String message = "aggregate id : %s , aggregate type: %s , processing timeout .";
            CompletableFuture<T> exceptionFuture = new CompletableFuture<>();
            exceptionFuture.completeExceptionally(new AggregateProcessingTimeoutException(
                    String.format(message, command.getAggregateId(), getAggregateType().getTypeName())
            ));
            return exceptionFuture;
        }
        try {
            T aggregate = supplier.get();
            return commitDomainEventAsync(aggregate, command.getShardingParams())
                    .thenCompose(result -> CompletableFuture.completedFuture(aggregate));
        } catch (Throwable e) {
            CompletableFuture<T> exceptionFuture = new CompletableFuture<>();
            exceptionFuture.completeExceptionally(e);
            return exceptionFuture;
        } finally {
            lock.unlock();
        }

    }

    /**
     * 聚合根业务处理
     *
     * @param command
     * @param function
     * @param lockWaitingTime 聚合根更新冲突时间，会暂停当前聚合根新的command的处理，直到聚合根恢复完成时才接受新的command。
     * @return
     * @throws AggregateEventConflictException     出现此异常的原因是当前聚合根在多个实例中存在（集群扩容时），可以捕获此异常然后重新在client发起调用，当前的请求会负载到新的实例上。
     * @throws EventStoreException                 持久化事件时出现预料之外的错误。
     * @throws AggregateProcessingTimeoutException 聚合根更新冲突时间，会暂停当前聚合根新的command的处理，如果超过lockWaitingTime时间还未执行，会抛出此异常。
     * @throws AggregateNotFoundException
     */
    @Override
    public <R> CompletableFuture<R> process(final Command command, final Function<T, R> function, Long lockWaitingTime) {
        checkNotNull(command);
        checkNotNull(command.getAggregateId());
        long aggregateId = command.getAggregateId();
        ReentrantLock lock = aggregateSlotLock.getLock(command.getAggregateId());
        boolean flag;
        try {
            flag = lock.tryLock(lockWaitingTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            String message = "aggregate id : %s , aggregate type: %s , processing timeout .";
            CompletableFuture<R> exceptionFuture = new CompletableFuture<>();
            exceptionFuture.completeExceptionally(new AggregateProcessingTimeoutException(
                    String.format(message, aggregateId, getAggregateType().getTypeName()), e
            ));
            return exceptionFuture;
        }

        if (!flag) {
            String message = "aggregate id : %s , aggregate type: %s , processing timeout .";
            CompletableFuture<R> exceptionFuture = new CompletableFuture<>();
            exceptionFuture.completeExceptionally(new AggregateProcessingTimeoutException(String.format(message, aggregateId, getAggregateType().getTypeName())));
            return exceptionFuture;
        }
        try {
            T aggregate = load(aggregateId, this.getAggregateType(), command.getShardingParams());
            R result = function.apply(aggregate);
            if (aggregate.getUncommittedEvent() == null) {
                return CompletableFuture.completedFuture(result);
            } else {
                return commitDomainEventAsync(aggregate, command.getShardingParams())
                        .thenCompose(futureResult -> CompletableFuture.completedFuture(result));
            }
        } catch (Throwable e) {
            CompletableFuture<R> exceptionFuture = new CompletableFuture<>();
            exceptionFuture.completeExceptionally(e);
            return exceptionFuture;
        } finally {
            lock.unlock();
        }
    }

    public <R> CompletableFuture<R> process(final Command command, final Function<T, R> function) {
        return this.process(command, function, LOCK_WAITTING_TIME);
    }

    public CompletableFuture<T> process(final Command command, final Supplier<T> supplier) {
        return this.process(command, supplier, LOCK_WAITTING_TIME);
    }

    private CompletableFuture<Void> commitDomainEventAsync(T aggregate, Map<String, Object> shardingParams) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Event uncommittedEvent = aggregate.getUncommittedEvent();
        EventCommittingContext context = EventCommittingContext.builder()
                .aggregateId(aggregate.getId())
                .aggregateTypeName(aggregate.getClass().getTypeName())
                .event(uncommittedEvent)
                .shardingParams(shardingParams)
                .future(future)
                .build();
        aggregate.acceptChanges();
        if (aggregate.reachSnapshotCycle(snapshotCycle())) {
            T snapsot = this.buildSnapshot(aggregate);
            context.setSnapshot(snapsot);
            log.debug("aggreaget id : {}, type : {}, version : {}, create snapshhot succeed.", snapsot.getId(), snapsot.getClass().getTypeName(), snapsot.getVersion());
        }
        eventCommittingService.commitDomainEventAsync(context);
        return future.thenAccept(result -> {
            if (aggregateCache.get(aggregate.getId()) == null) {
                aggregateCache.update(aggregate.getId(), aggregate);
            }

            if (context.getSnapshot() != null) {
                aggregateSnapshootService.saveAggregateSnapshot(context.getSnapshot());
                aggregate.refreshLastSnapTime();
            }
        });
    }

}
