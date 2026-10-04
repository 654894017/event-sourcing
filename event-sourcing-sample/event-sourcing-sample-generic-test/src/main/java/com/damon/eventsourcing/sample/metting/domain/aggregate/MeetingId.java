package com.damon.eventsourcing.sample.metting.domain.aggregate;

import lombok.Data;
import lombok.NonNull;

@Data
public class MeetingId {
    private final String meetingDate;
    private final String meettingNumber;

    public MeetingId(@NonNull String meetingDate, @NonNull String meettingNumber) {
        this.meetingDate = meetingDate;
        this.meettingNumber = meettingNumber;
    }

    public MeetingId(@NonNull Long meetingId) {
        if (meetingId == null) {
            throw new IllegalArgumentException("mettingId is null");
        }
        this.meetingDate = meetingId.toString().substring(0, 8);
        this.meettingNumber = meetingId.toString().substring(8);
    }

    public Long getId() {
        return Long.parseLong(meetingDate + meettingNumber);
    }

}
