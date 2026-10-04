package com.damon.eventsourcing.sample.red_packet.api;

import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketCreateCommand;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketGetCommand;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketGrabCommand;
import com.damon.eventsourcing.sample.red_packet.api.dto.WeixinRedPacketDTO;

public interface IRedPacketCommandService {
    /**
     * 创建红包
     *
     * @param command
     */
    void createRedPackage(RedPacketCreateCommand command);

    /**
     * 抢红包
     *
     * @param command
     * @return
     */
    int grabRedPackage(RedPacketGrabCommand command);

    /**
     * 红包详情
     *
     * @param command
     * @return
     */
    WeixinRedPacketDTO get(RedPacketGetCommand command);
}

