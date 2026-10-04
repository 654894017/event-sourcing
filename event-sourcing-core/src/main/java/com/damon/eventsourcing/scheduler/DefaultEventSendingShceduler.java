package com.damon.eventsourcing.scheduler;

import com.damon.eventsourcing.event.EventSendingContext;
import com.damon.eventsourcing.event.IEventSendService;
import com.damon.eventsourcing.store.IEventOffset;
import com.damon.eventsourcing.store.IEventStore;
import com.damon.eventsourcing.utils.NamedThreadFactory;
import com.damon.eventsourcing.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事件发送调度器
 *
 * @author xianping_lu
 */
@Slf4j
public class DefaultEventSendingShceduler implements IEventSendingShceduler {

    private final ScheduledExecutorService scheduledExecutorService;

    private final IEventOffset eventOffset;

    private final IEventStore eventStore;

    private final IEventSendService sendMessageService;

    public DefaultEventSendingShceduler(
            final IEventStore eventStore,
            final IEventOffset eventOffset,
            final IEventSendService sendMessageService,
            final int delaySeconds) {
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("event-scheduler-pool"));
        this.eventOffset = eventOffset;
        this.eventStore = eventStore;
        this.sendMessageService = sendMessageService;
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                sendEvent();
            } catch (Throwable e) {
                log.error("event sending failed", e);
            }
        }, 5, delaySeconds, TimeUnit.SECONDS);
    }

    @Override
    public void sendEvent() {
        for (; ; ) {
            List<Map<String, Object>> rows = eventOffset.queryEventOffset();
            AtomicInteger count = new AtomicInteger(0);
            for (Map<String, Object> map : rows) {
                Long eventOffsetId = (Long) map.get("event_offset_id");
                String dataSourceName = (String) map.get("data_source_name");
                String tableName = (String) map.get("table_name");
                Long id = (Long) map.get("id");
                List<EventSendingContext> contexts = eventStore.queryWaitingSendEvents(
                        dataSourceName, tableName, eventOffsetId
                );
                if (contexts.isEmpty()) {
                    count.addAndGet(1);
                    continue;
                }
                long offsetId = contexts.get(contexts.size() - 1).getOffsetId();
                log.info("event start offset id : {}， end offset id : {}, dataSourceName : {}, tableName: {}, id :{}",
                        eventOffsetId, offsetId, dataSourceName, tableName, id);
                try {
                    sendMessageService.sendMessage(contexts);
                    eventOffset.updateEventOffset(dataSourceName, offsetId, id);
                    log.info("update event offset id :  {}, dataSourceName : {}, tableName: {}, id :{} ", offsetId, dataSourceName, tableName, id);
                } catch (Exception e) {
                    log.error("sending event message failed", e);
                    ThreadUtils.sleep(10000);
                }
            }
            //所有表都检查一遍，如果无数据需要发送，则跳出循环。
            if (count.intValue() == rows.size()) {
                return;
            }
        }

    }
}
