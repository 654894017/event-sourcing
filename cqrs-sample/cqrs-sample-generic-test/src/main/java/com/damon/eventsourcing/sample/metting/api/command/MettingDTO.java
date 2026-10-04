package com.damon.eventsourcing.sample.metting.api.command;

import com.damon.eventsourcing.sample.metting.domain.aggregate.ReserveInfo;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Map;

@Data
@ToString
public class MettingDTO implements Serializable {

    private BitSet schedule;
    /**
     * 会议室日期
     */
    private String meetingDate;
    /**
     * 会议室预定记录
     */
    private Map<String, ReserveInfo> reserveRecord;

    public MettingDTO(BitSet schedule, String meetingDate, Map<String, ReserveInfo> reserveRecord) {
        this.schedule = schedule;
        this.meetingDate = meetingDate;
        this.reserveRecord = reserveRecord;
    }

    public MettingDTO() {
    }
}
