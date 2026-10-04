package com.damon.eventsourcing.store;

import java.util.List;
import java.util.Map;

public interface IEventOffset {

    void updateEventOffset(String dataSourceName, long offsetId, long id);

    List<Map<String, Object>> queryEventOffset();

}
