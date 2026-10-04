package com.damon.eventsourcing.event;

import java.util.List;

public interface IEventSendService {

    void sendMessage(List<EventSendingContext> contexts);

}
