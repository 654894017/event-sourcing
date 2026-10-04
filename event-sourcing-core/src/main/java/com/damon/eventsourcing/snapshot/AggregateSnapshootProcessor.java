package com.damon.eventsourcing.snapshot;

import com.damon.eventsourcing.EventSourcingContext;
import com.damon.eventsourcing.command.ICommandProcessor;
import com.damon.eventsourcing.domain.AggregateRoot;
import com.damon.eventsourcing.utils.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 聚合快照保存处理服务
 * 实现：缓冲聚合快照 + 定时分片入队 + 多线程异步持久化
 *
 * @author xianping_lu
 */
@Slf4j
public class AggregateSnapshootProcessor implements IAggregateSnapshootProcessor {

    /**
     * 定时任务首次延迟时间(秒)
     */
    private static final long SCHEDULE_INIT_DELAY = 5L;
    /**
     * 消费队列poll阻塞超时时间(秒)
     */
    private static final long QUEUE_POLL_TIMEOUT = 5L;
    /**
     * 默认分片队列容量
     */
    private static final int DEFAULT_QUEUE_SIZE = 4 * 1024;

    /**
     * 快照处理线程数量
     */
    private final int processThreadNum;
    /**
     * 定时刷新缓冲间隔秒数
     */
    private final int flushDelaySeconds;

    /**
     * 快照消费线程池
     */
    private final ExecutorService snapshotProcessPool;
    /**
     * 定时刷新缓冲任务调度器
     */
    private final ScheduledExecutorService scheduleExecutor;

    /**
     * 分片消费队列列表，按聚合ID哈希分片
     */
    private final List<LinkedBlockingQueue<AggregateRoot>> shardQueueList = new ArrayList<>();
    /**
     * 缓冲快照存储map，定时批量刷入分片队列
     */
    private final HashMap<Long, AggregateRoot> pendingSnapshotMap = new HashMap<>();
    /**
     * 缓冲map操作锁（公平锁）
     */
    private final ReentrantLock lock = new ReentrantLock(true);

    public AggregateSnapshootProcessor(int processThreadNum, int flushDelaySeconds) {
        this(processThreadNum, flushDelaySeconds, DEFAULT_QUEUE_SIZE);
    }

    public AggregateSnapshootProcessor(int processThreadNum, int flushDelaySeconds, int queueSize) {
        // 参数校验，启动阶段快速失败
        if (processThreadNum <= 0) {
            throw new IllegalArgumentException("快照处理线程数必须大于0");
        }
        if (flushDelaySeconds <= 0) {
            throw new IllegalArgumentException("定时刷新延迟秒数必须大于0");
        }
        if (queueSize <= 0) {
            throw new IllegalArgumentException("分片队列容量必须大于0");
        }
        this.processThreadNum = processThreadNum;
        this.flushDelaySeconds = flushDelaySeconds;

        // 1. 初始化分片队列
        initShardQueue(queueSize);
        // 2. 初始化消费线程池
        this.snapshotProcessPool = Executors.newFixedThreadPool(
                processThreadNum,
                new NamedThreadFactory("aggregate-snapshot-pool")
        );
        // 3. 初始化定时调度线程池
        this.scheduleExecutor = Executors.newSingleThreadScheduledExecutor();
        // 4. 启动分片消费任务
        startAllShardConsumer();
        // 5. 启动定时刷新缓冲任务
        startScheduleFlushTask();
    }


    /**
     * 接收聚合快照，存入内存缓冲等待定时落地
     *
     * @param snapshot 聚合根快照
     */
    @Override
    public void saveAggregateSnapshot(AggregateRoot snapshot) {
        lock.lock();
        try {
            pendingSnapshotMap.put(snapshot.getId(), snapshot);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 初始化分片阻塞队列
     */
    private void initShardQueue(int queueSize) {
        for (int i = 0; i < processThreadNum; i++) {
            shardQueueList.add(new LinkedBlockingQueue<>(queueSize));
        }
    }

    /**
     * 启动定时刷新缓冲任务
     */
    private void startScheduleFlushTask() {
        scheduleExecutor.scheduleWithFixedDelay(
                this::flushPendingSnapshotToShardQueue,
                SCHEDULE_INIT_DELAY,
                flushDelaySeconds,
                TimeUnit.SECONDS
        );
    }

    /**
     * 启动所有分片消费线程
     */
    private void startAllShardConsumer() {
        for (int shardIndex = 0; shardIndex < processThreadNum; shardIndex++) {
            final int idx = shardIndex;
            snapshotProcessPool.submit(() -> consumeSingleShardQueue(idx));
        }
    }


    /**
     * 将缓冲map中所有快照批量刷入对应分片队列，刷完清空缓冲
     */
    private void flushPendingSnapshotToShardQueue() {
        List<AggregateRoot> snapshotList;
        try {
            lock.lock();
            // 一次性取出全部快照并清空map，缩短锁持有时间
            snapshotList = new ArrayList<>(pendingSnapshotMap.values());
            pendingSnapshotMap.clear();
        } finally {
            lock.unlock();
        }

        if (snapshotList.isEmpty()) {
            return;
        }

        // 无锁遍历分发到分片队列
        for (AggregateRoot aggregate : snapshotList) {
            LinkedBlockingQueue<AggregateRoot> targetQueue = getTargetShardQueue(aggregate.getId());
            boolean enqueueSuccess = targetQueue.offer(aggregate);
            if (!enqueueSuccess) {
                log.warn("aggregate snapshot handle queue is full. aggregateId : {}, type : {}",
                        aggregate.getId(), aggregate.getClass().getTypeName());
            }
        }
    }

    /**
     * 根据聚合ID哈希路由到对应分片队列
     *
     * @param aggregateId 聚合唯一ID
     * @return 分片阻塞队列
     */
    private LinkedBlockingQueue<AggregateRoot> getTargetShardQueue(Long aggregateId) {
        int hash = aggregateId.hashCode();
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        int shardIndex = hash % processThreadNum;
        return shardQueueList.get(shardIndex);
    }


    /**
     * 单个分片队列无限循环消费持久化快照
     *
     * @param shardIndex 分片下标
     */
    private void consumeSingleShardQueue(int shardIndex) {
        LinkedBlockingQueue<AggregateRoot> queue = shardQueueList.get(shardIndex);
        while (true) {
            AggregateRoot aggregate = null;
            try {
                aggregate = queue.poll(QUEUE_POLL_TIMEOUT, TimeUnit.SECONDS);
                if (aggregate != null) {
                    String aggregateType = aggregate.getClass().getTypeName();
                    ICommandProcessor<AggregateRoot> commandService = EventSourcingContext.get(aggregateType);
                    commandService.saveAggregateSnapshot(aggregate);
                }
            } catch (Throwable e) {
                log.error("aggregate snapshot save failed, shardIndex:{}, aggregateId:{}, type:{}",
                        shardIndex, aggregate.getId(), aggregate.getClass().getTypeName(), e);
            }
        }
    }

}