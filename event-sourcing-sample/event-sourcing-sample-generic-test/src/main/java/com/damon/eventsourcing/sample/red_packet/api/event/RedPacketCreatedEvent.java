package com.damon.eventsourcing.sample.red_packet.api.event;

import com.damon.eventsourcing.domain.Event;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Stack;

@Data
public class RedPacketCreatedEvent extends Event {

    /**
     *
     */
    private static final long serialVersionUID = 2969013356235525800L;

    private Stack<BigDecimal> redpacketStack;

    private Long sponsorId;

    private BigDecimal money;

    private BigDecimal minMoney;

    private BigDecimal number;

    public RedPacketCreatedEvent() {
        super();
    }

    public RedPacketCreatedEvent(Stack<BigDecimal> redpacketStack) {
        super();
        this.redpacketStack = redpacketStack;
    }


}
