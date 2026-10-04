package com.damon.eventsourcing.recovery;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.damon.eventsourcing.EventSourcingContext;
import com.damon.eventsourcing.cache.IAggregateCache;
import com.damon.eventsourcing.command.ICommandProcessor;
import com.damon.eventsourcing.config.AggregateSlotLock;
import com.damon.eventsourcing.domain.AggregateRoot;
import com.damon.eventsourcing.domain.Event;
import com.damon.eventsourcing.exception.EventSourcingLoadException;
import com.damon.eventsourcing.exception.EventSourcingReplayException;
import com.damon.eventsourcing.store.IEventStore;
import com.damon.eventsourcing.utils.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 聚合根恢复服务
 */
@Slf4j
public class AggregateRecoveryProcessor {
    // 常量统一管理
    private static final int INIT_VERSION = 1;
    private static final int FULL_REPLAY_END_VERSION = Integer.MAX_VALUE;
    // 依赖注入
    private final IEventStore eventStore;
    private final IAggregateCache aggregateCache;
    private final AggregateSlotLock aggregateSlotLock;

    public AggregateRecoveryProcessor(
            IEventStore eventStore,
            IAggregateCache aggregateCache,
            AggregateSlotLock aggregateSlotLock
    ) {
        this.eventStore = eventStore;
        this.aggregateCache = aggregateCache;
        this.aggregateSlotLock = aggregateSlotLock;
    }

    public <T extends AggregateRoot> void recover(
            Long aggregateId,
            String aggregateType,
            Map<String, Object> shardingParams
    ) {
        ReentrantLock lock = aggregateSlotLock.getLock(aggregateId);
        try {
            lock.lock();
            // 1. 构建聚合快照
            T aggregate = buildSnapshot(aggregateId, aggregateType);
            // 2. 计算回放起始版本
            int startVersion = aggregate.isNew() ? INIT_VERSION : aggregate.getVersion() + 1;
            // 3. 执行事件回放并更新缓存
            replayEvents(aggregate, startVersion, FULL_REPLAY_END_VERSION, shardingParams);
        } finally {
            lock.unlock();
        }
    }

    private <T extends AggregateRoot> T buildSnapshot(Long aggregateId, String aggregateType) {
        Class<T> aggregateCls = ReflectUtils.getClass(aggregateType);
        ICommandProcessor<T> processor = EventSourcingContext.get(aggregateType);
        T snapshot = processor.getAggregateSnapshot(aggregateId, aggregateCls);

        if (ObjectUtil.isNull(snapshot)) {
            snapshot = ReflectUtils.newInstance(aggregateCls);
            snapshot.setId(aggregateId);
            log.info("[快照构建] 无历史快照，新建空聚合根 aggregateId={}, type={}", aggregateId, aggregateType);
        }
        return snapshot;
    }

    private <T extends AggregateRoot> void replayEvents(
            T aggregate,
            int startVersion,
            int endVersion,
            Map<String, Object> shardingParams
    ) {

        final Long aggId = aggregate.getId();
        final String aggTypeName = aggregate.getClass().getTypeName();
        log.info("[事件回放] 开始执行，aggregateId={}, type={}, startVersion={}, endVersion={}",
                aggId, aggTypeName, startVersion, endVersion);

        // 1：加载增量事件
        List<Event> eventBatches = loadEvents(aggregate, aggId, aggTypeName, startVersion, endVersion, shardingParams);

        // 2：批量回放事件
        replayEventBatches(aggregate, aggId, aggTypeName, startVersion, endVersion, eventBatches);

        // 3：更新缓存（独立异常捕获，不阻断回放成功流程）
        refreshAggregateCache(aggregate, aggId, aggTypeName);
    }

    /**
     * 加载指定版本区间的批量事件
     */
    private List<Event> loadEvents(AggregateRoot aggregate,
                                   Long aggId,
                                   String aggTypeName,
                                   int startVersion,
                                   int endVersion,
                                   Map<String, Object> shardingParams) {
        long loadStart = System.currentTimeMillis();
        try {
            List<Event> events = eventStore.load(aggId, aggregate.getClass(), startVersion, endVersion, shardingParams);
            long costMs = System.currentTimeMillis() - loadStart;
            int batchNum = CollUtil.isEmpty(events) ? 0 : events.size();
            log.info("[事件加载] 完成 aggregateId={}, batchCount={}, 耗时={}ms", aggId, batchNum, costMs);
            return events;
        } catch (Exception e) {
            String errMsg = buildLoadErrMsg(aggId, aggTypeName, startVersion, endVersion);
            throw new EventSourcingLoadException(errMsg, e);
        }
    }

    /**
     * 批量执行事件回放
     */
    private void replayEventBatches(AggregateRoot aggregate,
                                    Long aggId,
                                    String aggTypeName,
                                    int startVersion,
                                    int endVersion,
                                    List<Event> events) {
        long replayStart = System.currentTimeMillis();
        try {
            aggregate.replayEvents(events);
        } catch (Exception e) {
            String errMsg = buildReplayErrMsg(aggId, aggTypeName, startVersion, endVersion);
            throw new EventSourcingReplayException(errMsg, e);
        }
        long costMs = System.currentTimeMillis() - replayStart;
        log.info("[事件回放] 完成 aggregateId={}, 耗时={}ms", aggId, costMs);
    }

    /**
     * 更新聚合缓存，缓存失败单独告警，不抛出异常中断流程
     */
    private void refreshAggregateCache(AggregateRoot aggregate, Long aggId, String aggTypeName) {
        aggregateCache.update(aggId, aggregate);
        log.info("[聚合恢复] 全部流程成功 aggregateId={}, type={}", aggId, aggTypeName);
    }

    private String buildLoadErrMsg(Long aggId, String type, int start, int end) {
        return String.format("事件加载失败，aggregateId=%s, type=%s, startVersion=%s, endVersion=%s",
                aggId, type, start, end);
    }

    private String buildReplayErrMsg(Long aggId, String type, int start, int end) {
        return String.format("事件回放失败，aggregateId=%s, type=%s, startVersion=%s, endVersion=%s",
                aggId, type, start, end);
    }
}