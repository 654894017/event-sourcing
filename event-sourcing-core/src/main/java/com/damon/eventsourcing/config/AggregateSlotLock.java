package com.damon.eventsourcing.config;

import com.damon.eventsourcing.domain.AggregateRoot;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 聚合根锁
 */
@Data
@Slf4j
public class AggregateSlotLock {
    private final List<ReentrantLock> locks = new ArrayList<>();
    private final int lockNumber;
    private final Cache<Long, Boolean> aggregateExceptionFlag;


    public AggregateSlotLock(int lockNumber) {
        this.lockNumber = lockNumber;
        for (int i = 0; i < lockNumber; i++) {
            locks.add(new ReentrantLock(true));
        }
        aggregateExceptionFlag = Caffeine.newBuilder().expireAfterWrite(6, TimeUnit.MINUTES)
                .maximumSize(1024 * 10).removalListener((key, value, cause) -> {
                    Long aggregateId = (Long) key;
                    AggregateRoot aggregate = (AggregateRoot) value;
                    log.info("aggregate id : {}, aggregate type : {}, version:{}, expired.",
                            aggregateId, aggregate.getClass().getTypeName(), aggregate.getVersion()
                    );
                }).build();
    }

    public ReentrantLock getLock(Long aggregateId) {
        int hash = aggregateId.hashCode();
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        int index = hash % lockNumber;
        return locks.get(index);
    }

}
