package com.damon.eventsourcing.store;

import java.util.Map;

/**
 * 聚合路由
 * <p>
 * 1个实例对应n个分片
 *
 * @author xianpinglu
 */
public interface IEventShardingRouting {
    /**
     * 路由到实例
     *
     * @param aggregateId    聚合根id
     * @param aggregateType  聚合根列席
     * @param instanceNumber 实例数量
     * @param shardingParams 自定义分片参数
     * @return
     */
    Integer routeInstance(Long aggregateId, String aggregateType, Integer instanceNumber, Map<String, Object> shardingParams);

    /**
     * 路由到分片
     *
     * @param aggregateId    聚合根id
     * @param aggregateType  聚合根类型
     * @param shardingNumber 分片数量
     * @param shardingParams 自定义分片参数
     * @return
     */
    Integer routeSharding(Long aggregateId, String aggregateType, Integer shardingNumber, Map<String, Object> shardingParams);
}
