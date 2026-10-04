package com.damon.eventsourcing.event_store;

import com.damon.eventsourcing.exception.EventOfffsetUpdateException;
import com.damon.eventsourcing.exception.EventQueryException;
import com.damon.eventsourcing.store.IEventOffset;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * 事件偏移位置存储
 * <p>
 * 用于记录event store已发送的事件的偏移位置
 *
 * @author xianping_lu
 */
public class MysqlEventOffset implements IEventOffset {

    private final String QUERY_EVENT_OFFSET = "SELECT id, event_offset_id, data_source_name, table_name FROM event_offset";

    private final String UPDATE_EVENT_OFFSET = "UPDATE event_offset SET event_offset_id = ? where id = ? ";

    private final Map<String, DataSource> dataSourceNameMap;

    public MysqlEventOffset(final List<DataSourceMapping> dataSourceMappings) {
        dataSourceNameMap = new HashMap<>();
        dataSourceMappings.forEach(mapping -> {
            dataSourceNameMap.put(mapping.getDataSourceName(), mapping.getDataSource());
        });
    }

    @Override
    public List<Map<String, Object>> queryEventOffset() {
        List<Map<String, Object>> list = new ArrayList<>();
        Set<String> set = dataSourceNameMap.keySet();
        for (String name : set) {
            DataSource dataSource = dataSourceNameMap.get(name);
            QueryRunner queryRunner = new QueryRunner(dataSource);
            List<Map<String, Object>> rows = null;
            try {
                rows = queryRunner.query(QUERY_EVENT_OFFSET, new MapListHandler());
            } catch (SQLException e) {
                throw new EventQueryException(e);
            }
            list.addAll(rows);
        }
        return list;

    }

    @Override
    public void updateEventOffset(String dataSourceName, long offsetId, long id) {
        DataSource dataSource = dataSourceNameMap.get(dataSourceName);
        QueryRunner queryRunner = new QueryRunner(dataSource);
        int result = 0;
        try {
            result = queryRunner.update(UPDATE_EVENT_OFFSET, offsetId, id);
        } catch (SQLException e) {
            throw new EventOfffsetUpdateException(e);
        }
        if (result == 0) {
            throw new EventOfffsetUpdateException("update event offset failed");
        }

    }
}
