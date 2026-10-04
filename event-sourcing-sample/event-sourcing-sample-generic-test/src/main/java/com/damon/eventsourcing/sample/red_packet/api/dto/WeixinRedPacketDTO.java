package com.damon.eventsourcing.sample.red_packet.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Stack;

/**
 * @author xianpinglu
 */
@Data
public class WeixinRedPacketDTO {
    /**
     * key 用户id   value  抢到的金额
     */
    private Map<Long, BigDecimal> map;

    private Stack<BigDecimal> redpacketStack;

    private Long sponsorId;

    private Long id;

}
