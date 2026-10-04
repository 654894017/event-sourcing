package com.damon.eventsourcing.sample;

import com.damon.eventsourcing.cache.CaffeineAggregateCache;
import com.damon.eventsourcing.cache.IAggregateCache;
import com.damon.eventsourcing.config.AggregateSlotLock;
import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.event.EventCommittingService;
import com.damon.eventsourcing.event_store.DataSourceMapping;
import com.damon.eventsourcing.event_store.DefaultEventShardingRouting;
import com.damon.eventsourcing.event_store.MysqlEventOffset;
import com.damon.eventsourcing.event_store.MysqlEventStore;
import com.damon.eventsourcing.recovery.AggregateRecoveryProcessor;
import com.damon.eventsourcing.snapshot.AggregateSnapshootProcessor;
import com.damon.eventsourcing.snapshot.IAggregateSnapshootProcessor;
import com.damon.eventsourcing.store.IEventOffset;
import com.damon.eventsourcing.store.IEventStore;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariDataSource;

import java.util.List;

public class TestConfig {
    private static String bootstrapServers = "xxxx";

    public static HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/cqrs?serverTimezone=UTC&rewriteBatchedStatements=true");
        dataSource.setUsername("root");
        dataSource.setPassword("mysqlroot");
        dataSource.setMaximumPoolSize(20);
        dataSource.setMinimumIdle(20);
        dataSource.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getTypeName());
        return dataSource;
    }

    public static EventSourcingConfig init() {
        List<DataSourceMapping> list = Lists.newArrayList(
                DataSourceMapping.builder().dataSourceName("ds0").dataSource(dataSource()).tableNumber(1).build()
        );

        DefaultEventShardingRouting route = new DefaultEventShardingRouting();
        IEventStore store = new MysqlEventStore(list, 8, route);
        IEventOffset offset = new MysqlEventOffset(list);
        IAggregateSnapshootProcessor aggregateSnapshootService = new AggregateSnapshootProcessor(1, 6);
        IAggregateCache aggregateCache = new CaffeineAggregateCache(1024 * 1024, 60);

        //如果event走cdc模式,不用初始化
        //initEventListener(store, offset);

        AggregateSlotLock aggregateSlotLock = new AggregateSlotLock(4096);
        AggregateRecoveryProcessor aggregateRecoveryProcessor = new AggregateRecoveryProcessor(store, aggregateCache, aggregateSlotLock);
        EventCommittingService eventCommittingService = new EventCommittingService(
                store, 8, 1024 * 4, 32, aggregateRecoveryProcessor
        );

        EventSourcingConfig eventSourcingConfig = EventSourcingConfig.builder().
                eventStore(store).aggregateSnapshootService(aggregateSnapshootService).aggregateCache(aggregateCache).
                aggregateSlotLock(aggregateSlotLock).
                eventCommittingService(eventCommittingService).build();
        return eventSourcingConfig;
    }

//    private static void initEventListener(IEventStore store, IEventOffset offset) {
//        KafkaProducerConfig producerConfig = new KafkaProducerConfig(bootstrapServers, "event_queue");
//        IEventSendService sendingService = new KafkaEventSendService(producerConfig);
//        new DefaultEventSendingShceduler(store, offset, sendingService, 5);
//        KafkaConsumerConfig consumerConfig = new KafkaConsumerConfig(bootstrapServers, "event_queue", "a1");
//        consumerConfig.setTopic("event_queue");
//        consumerConfig.setGroupId("test_123");
//        consumerConfig.setBootstrapServers(bootstrapServers);
//        new GoodsEventListener(consumerConfig);
//    }


}
