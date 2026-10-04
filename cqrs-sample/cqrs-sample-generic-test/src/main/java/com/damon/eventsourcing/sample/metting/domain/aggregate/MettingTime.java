package com.damon.eventsourcing.sample.metting.domain.aggregate;

import com.damon.eventsourcing.domain.ValueObject;
import com.damon.eventsourcing.sample.metting.api.MettingConstants;
import lombok.NonNull;


public class MettingTime implements ValueObject {
    private Integer start;
    private Integer end;

    public MettingTime(@NonNull Integer start, @NonNull Integer end) {
        if (start >= end) {
            throw new IllegalArgumentException("会议时间范围无效");
        }
        if (start < 0 || start > MettingConstants.METTIING_TIME_SLOTS) {
            throw new IllegalArgumentException("会议开始时间无效");
        }
        if (end < 1 || end > MettingConstants.METTIING_TIME_SLOTS) {
            throw new IllegalArgumentException("会议结束时间无效");
        }
        this.start = start;
        this.end = end;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

}
