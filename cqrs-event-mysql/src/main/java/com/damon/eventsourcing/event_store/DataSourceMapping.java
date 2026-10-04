package com.damon.eventsourcing.event_store;

import lombok.Builder;
import lombok.Data;

import javax.sql.DataSource;

@Data
@Builder
public class DataSourceMapping {

    private String dataSourceName;

    private DataSource dataSource;

    private int tableNumber;
}
