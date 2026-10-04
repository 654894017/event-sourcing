package com.damon.eventsourcing.command;

import com.damon.eventsourcing.domain.AggregateRoot;
import com.damon.eventsourcing.domain.Command;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 聚合领域服务抽象类
 * <p>
 * 可以在此服务上封装dubbo、spring cloud 微服务框架。
 * <p>
 * 注意：负载均衡需要采用hash机制，建议使用一致性hash，当集群扩容、缩容时对聚合根的恢复影响较小。
 *
 * @author xianping_lu
 */
public interface ICommandProcessor<T extends AggregateRoot> {
    /**
     * @param command
     * @param supplier
     * @param lockWaitingTime 获取锁等待时间（毫秒）
     * @return
     */
    CompletableFuture<T> process(final Command command, final Supplier<T> supplier, Long lockWaitingTime);

    /**
     * @param <R>
     * @param command
     * @param function
     * @param lockWaitingTime 获取锁等待时间（毫秒）
     * @return
     */
    <R> CompletableFuture<R> process(final Command command, final Function<T, R> function, Long lockWaitingTime);

    /**
     * 获取聚合快照，用于加速聚合回溯(对于聚合存在的生命周期特别长且修改特别频繁时需要实现)
     * <p>
     * 逻辑：先判断是否存在聚合快照，如果不存在聚合快照，从Q端恢复聚合。
     *
     * @param aggregateId
     * @param classes
     * @return
     */
    default T getAggregateSnapshot(long aggregateId, Class<T> classes) {
        return null;
    }

    /**
     * 保存聚合快照
     * <p>
     * 可以保存到类似redis高性能缓存中（不用担心丢失，Q端、Event库中都存有聚合数据信息）
     *
     * @param aggregate
     * @return
     */
    default CompletableFuture<Boolean> saveAggregateSnapshot(T aggregate) {
        return CompletableFuture.completedFuture(true);
    }

    /**
     * 创建聚合根快照
     *
     * @param aggregate
     * @return
     */
    default T buildSnapshot(T aggregate) {
        return null;
    }

    /**
     * 聚合根快照创建周期（单位秒），小于0不创建快照
     *
     * @return
     */
    default long snapshotCycle() {
        return -1;
    }

}
