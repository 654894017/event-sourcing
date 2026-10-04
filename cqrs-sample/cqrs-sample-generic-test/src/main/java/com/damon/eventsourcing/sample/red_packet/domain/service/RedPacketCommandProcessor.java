package com.damon.eventsourcing.sample.red_packet.domain.service;

import com.damon.eventsourcing.command.CommandProcessor;
import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.sample.red_packet.api.IRedPacketCommandService;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketCreateCommand;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketGetCommand;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketGrabCommand;
import com.damon.eventsourcing.sample.red_packet.api.dto.WeixinRedPacketDTO;
import com.damon.eventsourcing.sample.red_packet.domain.aggregate.WeixinRedPacket;

import java.util.concurrent.CompletableFuture;

/**
 * @author xianpinglu
 */
public class RedPacketCommandProcessor extends CommandProcessor<WeixinRedPacket> implements IRedPacketCommandService {

    public RedPacketCommandProcessor(EventSourcingConfig eventSourcingConfig) {
        super(eventSourcingConfig);
    }

    @Override
    public void createRedPackage(final RedPacketCreateCommand command) {
        super.process(command, () -> new WeixinRedPacket(command)).join();
    }

    @Override
    public int grabRedPackage(final RedPacketGrabCommand command) {
        return super.process(command, redPacket -> redPacket.grabRedPackage(command)).join();
    }

    @Override
    public WeixinRedPacketDTO get(final RedPacketGetCommand command) {
        CompletableFuture<WeixinRedPacketDTO> future = super.process(
                command,
                redPacket -> {
                    WeixinRedPacketDTO redPacketDTO = new WeixinRedPacketDTO();
                    redPacketDTO.setMap(redPacket.getMap());
                    redPacketDTO.setId(redPacket.getId());
                    redPacketDTO.setRedpacketStack(redPacket.getRedpacketStack());
                    redPacketDTO.setSponsorId(redPacket.getSponsorId());
                    return redPacketDTO;
                }
        );
        return future.join();
    }


    @Override
    public CompletableFuture<Boolean> saveAggregateSnapshot(WeixinRedPacket aggregate) {
        return super.saveAggregateSnapshot(aggregate);
    }

    @Override
    public WeixinRedPacket buildSnapshot(WeixinRedPacket aggregate) {
        return super.buildSnapshot(aggregate);
    }

    @Override
    public long snapshotCycle() {
        return super.snapshotCycle();
    }
}
