package com.damon.eventsourcing.sample.red_packet.api.command;

import com.damon.eventsourcing.domain.Command;

public class RedPacketGrabCommand extends Command {

    /**
     *
     */
    private static final long serialVersionUID = -3309773599641095159L;
    private Long userId;

    public RedPacketGrabCommand(Long redPacketId) {
        super(redPacketId);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
