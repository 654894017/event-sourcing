package com.damon.eventsourcing.cache;

import com.damon.eventsourcing.domain.AggregateRoot;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 基于caffeine聚合根缓存
 *
 * @author xianpinglu
 */
@Slf4j
public class CaffeineAggregateCache implements IAggregateCache {

    private final Cache<Long, AggregateRoot> aggregateCache;

    /**
     * @param cacheMaximumSize 最多能够缓存多少聚合个数
     * @param expireTime       有效时间（分钟）
     */
    public CaffeineAggregateCache(int cacheMaximumSize, int expireTime) {
        aggregateCache = Caffeine.newBuilder().expireAfterWrite(expireTime, TimeUnit.MINUTES)
                .maximumSize(cacheMaximumSize).removalListener((key, value, cause) -> {
                    Long aggregateId = (Long) key;
                    AggregateRoot aggregate = (AggregateRoot) value;
                    log.info("aggregate id : {}, aggregate type : {}, version:{}, expired.",
                            aggregateId, aggregate.getClass().getTypeName(), aggregate.getVersion()
                    );
                }).build();
    }

    @Override
    public void update(long id, AggregateRoot aggregate) {
        aggregateCache.put(id, aggregate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends AggregateRoot> T get(long id) {
        return (T) aggregateCache.getIfPresent(id);
    }

}
