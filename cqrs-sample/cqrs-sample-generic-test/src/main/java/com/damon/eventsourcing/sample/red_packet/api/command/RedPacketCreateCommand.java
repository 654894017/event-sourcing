package com.damon.eventsourcing.sample.red_packet.api.command;

import com.damon.eventsourcing.domain.Command;

import java.math.BigDecimal;

public class RedPacketCreateCommand extends Command {

    /**
     *
     */
    private static final long serialVersionUID = -2434884336800045268L;

    private BigDecimal money;

    private BigDecimal number;
    /**
     * 红包发起人id
     */
    private Long sponsorId;

    private BigDecimal minMoney;

    /**
     * @param commandId
     * @param aggregateId
     */
    public RedPacketCreateCommand(long aggregateId) {
        super(aggregateId);
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public Long getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(Long sponsorId) {
        this.sponsorId = sponsorId;
    }

    public BigDecimal getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(BigDecimal minMoney) {
        this.minMoney = minMoney;
    }


}
