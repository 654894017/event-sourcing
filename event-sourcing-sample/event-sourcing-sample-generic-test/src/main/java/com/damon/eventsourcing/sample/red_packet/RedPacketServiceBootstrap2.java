package com.damon.eventsourcing.sample.red_packet;

import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.sample.TestConfig;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketCreateCommand;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketGrabCommand;
import com.damon.eventsourcing.sample.red_packet.domain.service.RedPacketCommandProcessor;
import com.damon.eventsourcing.utils.IdWorker;

import java.math.BigDecimal;


public class RedPacketServiceBootstrap2 {
    public static void main(String[] args) {
        EventSourcingConfig eventSourcingConfig = TestConfig.init();
        RedPacketCommandProcessor redPacketServcie = new RedPacketCommandProcessor(eventSourcingConfig);
        RedPacketCreateCommand create = new RedPacketCreateCommand(1L);
        create.setMoney(new BigDecimal(20000));
        create.setNumber(new BigDecimal(10));
        create.setMinMoney(new BigDecimal(1));
        create.setSponsorId(1L);
        redPacketServcie.createRedPackage(create);


        for (int number = 0; number < 5; number++) {
            RedPacketGrabCommand grabCommand = new RedPacketGrabCommand(1L);
            grabCommand.setUserId(IdWorker.getId());
            int status = redPacketServcie.grabRedPackage(grabCommand);
            if (status <= 0) {
                System.out.println("failed");
            }
        }

    }

}

