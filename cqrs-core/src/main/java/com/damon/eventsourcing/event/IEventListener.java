package com.damon.eventsourcing.event;

import com.damon.eventsourcing.domain.Event;

import java.util.List;

/**
 * 领域事件监听器
 *
 * @author xianpinglu
 */
public interface IEventListener {

    /**
     * 处理领域事件
     * <p>
     * 注意：当前方法内部不需要try catch 否则会ack当前消息,当前事件已经按照分区进行分组，业务可以考虑批量处理。
     *
     * @param events
     */
    void process(List<List<Event>> events);
}
