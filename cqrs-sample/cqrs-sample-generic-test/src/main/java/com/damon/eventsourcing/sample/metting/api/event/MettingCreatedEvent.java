package com.damon.eventsourcing.sample.metting.api.event;

import com.damon.eventsourcing.domain.Event;


public class MettingCreatedEvent extends Event {

    /**
     * 会议室日期
     */
    private String meetingDate;

    public MettingCreatedEvent() {
    }

    public MettingCreatedEvent(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }


}
