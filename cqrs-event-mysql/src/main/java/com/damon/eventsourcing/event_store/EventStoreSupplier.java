package com.damon.eventsourcing.event_store;

import com.alibaba.fastjson.JSONObject;
import com.damon.eventsourcing.domain.Event;
import com.damon.eventsourcing.event.AggregateEventAppendResult;
import com.damon.eventsourcing.event.DomainEventStream;
import com.damon.eventsourcing.exception.AggregateEventConflictException;
import com.damon.eventsourcing.exception.EventStoreException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EventStoreSupplier implements Supplier<AggregateEventAppendResult> {
    private static final Pattern PATTERN_MYSQL = Pattern.compile("^Duplicate entry '(.*)-(.*)' for key");
    private static final String INSERT_AGGREGATE_EVENTS = "INSERT INTO %s (aggregate_root_type_name, aggregate_root_id, event_type, event, version, gmt_create) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_STATE_CONFLICT = "23000";
    private static final String EVENT_TABLE_VERSION_UNIQUE_INDEX_NAME = "uk_aggregate_id_version";

    private final DataSource dataSource;
    private final String tableName;
    private final List<DomainEventStream> eventStreams;

    public EventStoreSupplier(DataSource dataSource, String tableName, List<DomainEventStream> eventStreams) {
        this.dataSource = dataSource;
        this.tableName = tableName;
        this.eventStreams = eventStreams;
    }

    @Override
    public AggregateEventAppendResult get() {
        AggregateEventAppendResult result = new AggregateEventAppendResult();
        QueryRunner queryRunner = new QueryRunner(dataSource);
        Map<Long, String> aggregateTypeMap = new HashMap<>();
        List<Object[]> batchParams = new ArrayList<>();

        prepareBatchParams(aggregateTypeMap, batchParams);

        try {
            queryRunner.batch(String.format(INSERT_AGGREGATE_EVENTS, tableName), batchParams.toArray(new Object[0][]));
            addSuccessResults(result);
        } catch (SQLException exception) {
            handleSQLException(exception, result);
        } catch (Throwable exception) {
            log.error("Store event failed", exception);
            handleUnexpectedException(exception, result);
        }

        return result;
    }

    private void prepareBatchParams(Map<Long, String> aggregateTypeMap, List<Object[]> batchParams) {
        eventStreams.forEach(stream -> {
            String eventType = stream.getEvent().getClass().getName();
            Event event = stream.getEvent();
            batchParams.add(new Object[]{
                    stream.getAggregateType(), stream.getAggregateId(), eventType, JSONObject.toJSONString(event), event.getVersion(), LocalDateTime.now()
            });
            aggregateTypeMap.put(stream.getAggregateId(), stream.getAggregateType());
        });
    }

    private void addSuccessResults(AggregateEventAppendResult result) {
        eventStreams.forEach(stream -> {
            AggregateEventAppendResult.SucceedResult succeedResult = new AggregateEventAppendResult.SucceedResult();
            succeedResult.setVersion(stream.getVersion());
            succeedResult.setAggregateType(stream.getAggregateType());
            succeedResult.setAggregateId(stream.getAggregateId());
            result.addSuccedResult(succeedResult);
        });
    }

    private void handleSQLException(SQLException exception, AggregateEventAppendResult result) {
        log.warn("Failed to store events. SQL state: {}, message: {}", exception.getSQLState(), exception.getMessage(), exception);
        if (SQL_STATE_CONFLICT.equals(exception.getSQLState())
                && exception.getMessage() != null
                && exception.getMessage().contains(EVENT_TABLE_VERSION_UNIQUE_INDEX_NAME)) {
            handleVersionConflict(exception, result);
        } else {
            handleUnexpectedException(exception, result);
        }
    }

    private void handleVersionConflict(SQLException exception, AggregateEventAppendResult result) {
        eventStreams.forEach(stream -> {
            AggregateEventAppendResult.DuplicateEventResult duplicateEventResult = new AggregateEventAppendResult.DuplicateEventResult();
            duplicateEventResult.setAggreateId(stream.getAggregateId());
            duplicateEventResult.setAggregateType(stream.getAggregateType());
            duplicateEventResult.setThrowable(new AggregateEventConflictException(stream.getAggregateId(), stream.getAggregateType(), exception));
            result.addDuplicateEventResult(duplicateEventResult);
        });
    }

    private void handleUnexpectedException(Throwable exception, AggregateEventAppendResult result) {
        eventStreams.forEach(stream -> {
            AggregateEventAppendResult.ExceptionResult exceptionResult = new AggregateEventAppendResult.ExceptionResult();
            exceptionResult.setThrowable(new EventStoreException("Event store exception", exception));
            exceptionResult.setAggreateId(stream.getAggregateId());
            exceptionResult.setAggregateType(stream.getAggregateType());
            result.addExceptionResult(exceptionResult);
        });
    }

    private String getExceptionId(String message, int index) {
        Matcher matcher = PATTERN_MYSQL.matcher(message);
        if (matcher.find() && matcher.groupCount() >= index) {
            return matcher.group(index);
        }
        return "";
    }
}