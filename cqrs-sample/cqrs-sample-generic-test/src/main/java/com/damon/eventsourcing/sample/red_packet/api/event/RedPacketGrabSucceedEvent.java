package com.damon.eventsourcing.sample.red_packet.api.event;

import com.damon.eventsourcing.domain.Event;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RedPacketGrabSucceedEvent extends Event {

    /**
     *
     */
    private static final long serialVersionUID = 8892155336754024236L;
    private BigDecimal money;
    private Long userId;
    private Long redPacketId;

    public RedPacketGrabSucceedEvent() {
        super();
    }

    public RedPacketGrabSucceedEvent(BigDecimal money, Long userId) {
        this.money = money;
        this.userId = userId;
    }

}
