package com.damon.eventsourcing.sample.red_packet;

import cn.hutool.core.util.IdUtil;
import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.sample.TestConfig;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketGrabCommand;
import com.damon.eventsourcing.sample.red_packet.domain.service.RedPacketCommandProcessor;


public class RedPacketServiceVirtualThreadBootstrap2 {

    public static void main(String[] args) throws InterruptedException {
        EventSourcingConfig eventSourcingConfig = TestConfig.init();
        RedPacketCommandProcessor redPacketServcie = new RedPacketCommandProcessor(eventSourcingConfig);
//        RedPacketCreateCommand create = new RedPacketCreateCommand(1);
//        create.setMoney(new BigDecimal(20000));
//        create.setNumber(new BigDecimal(10));
//        create.setMinMoney(new BigDecimal(1));
//        create.setSponsorId(1L);
//        redPacketServcie.createRedPackage(create);


        RedPacketGrabCommand grabCommand = new RedPacketGrabCommand(1L);
        grabCommand.setUserId(IdUtil.getSnowflakeNextId());
        int status = redPacketServcie.grabRedPackage(grabCommand);
        System.out.println(status);

    }


}

