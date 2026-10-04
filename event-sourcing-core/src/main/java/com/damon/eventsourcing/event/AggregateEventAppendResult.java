package com.damon.eventsourcing.event;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class AggregateEventAppendResult {

    private List<SucceedResult> succeedResults = Collections.synchronizedList(new ArrayList<>());

    private List<DuplicateEventResult> duplicateEventResults = Collections.synchronizedList(new ArrayList<>());

    private List<ExceptionResult> exceptionResults = Collections.synchronizedList(new ArrayList<>());

    public void addSuccedResult(SucceedResult result) {
        succeedResults.add(result);
    }

    public void addDuplicateEventResult(DuplicateEventResult result) {
        duplicateEventResults.add(result);
    }

    public void addExceptionResult(ExceptionResult result) {
        exceptionResults.add(result);
    }

    @Data
    public static class SucceedResult {

        private Long aggregateId;

        private String aggregateType;

        private Long commandId;

        private Integer version;

    }

    @Data
    public static class DuplicateEventResult {

        private Long aggreateId;

        private String aggregateType;

        private Throwable throwable;

    }

    @Data
    public static class ExceptionResult {

        private Long aggreateId;

        private String aggregateType;

        private Throwable throwable;

    }

}
