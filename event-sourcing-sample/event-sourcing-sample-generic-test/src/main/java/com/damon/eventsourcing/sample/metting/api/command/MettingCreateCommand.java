package com.damon.eventsourcing.sample.metting.api.command;

import com.damon.eventsourcing.domain.Command;

public class MettingCreateCommand extends Command {

    private final String meetingDate;

    public MettingCreateCommand(Long meetingId, String meetingDate) {
        super(meetingId);
        this.meetingDate = meetingDate;
    }

    public String getMeetingDate() {
        return meetingDate;
    }


}
