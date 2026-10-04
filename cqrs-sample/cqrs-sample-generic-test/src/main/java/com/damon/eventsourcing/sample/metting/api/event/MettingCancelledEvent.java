package com.damon.eventsourcing.sample.metting.api.event;

import com.damon.eventsourcing.domain.Event;
import lombok.Data;

@Data
public class MettingCancelledEvent extends Event {

    private String reserveFlag;
    private int start;
    private int end;

    public MettingCancelledEvent() {

    }

    public MettingCancelledEvent(String reserveFlag, int start, int end) {
        this.reserveFlag = reserveFlag;
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }


}
