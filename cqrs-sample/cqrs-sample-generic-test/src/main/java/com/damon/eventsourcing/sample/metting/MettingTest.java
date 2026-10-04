package com.damon.eventsourcing.sample.metting;

import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.sample.TestConfig;
import com.damon.eventsourcing.sample.metting.api.command.MettingCancelCommand;
import com.damon.eventsourcing.sample.metting.api.command.MettingGetCommand;
import com.damon.eventsourcing.sample.metting.api.command.MettingReserveCommand;
import com.damon.eventsourcing.sample.metting.domain.MettingCommandProcessor;
import com.damon.eventsourcing.sample.metting.domain.aggregate.MeetingId;
import com.damon.eventsourcing.sample.metting.domain.aggregate.MettingTime;
import com.damon.eventsourcing.sample.metting.domain.aggregate.ReseveStatus;

public class MettingTest {

    public static void main(String[] args) throws NoSuchMethodException {
        EventSourcingConfig eventSourcingConfig = TestConfig.init();
        MettingCommandProcessor commandService = new MettingCommandProcessor(eventSourcingConfig);
        Long userId = 181987L;
        String meetingDate = "20230320";
        String mettingNumber = "1103";

        MeetingId meetingId = new MeetingId(meetingDate, "1103");
        MettingReserveCommand reserveCommand = new MettingReserveCommand(
                //预定 0点到 10点会议(大等于0小于9:59)
                meetingId.getId(),
                userId,
                new MettingTime(0 * 60, 10 * 60), "UC权限接入议题",
                meetingDate,
                mettingNumber,
                "UC权限接入议题",
                "http://xxxxxxxx.xlsx"
        );
        //预定1103会议室
        ReseveStatus reseveStatus = commandService.reserve(reserveCommand);
        //获取1103会议室预定情况
        System.out.println(commandService.get(new MettingGetCommand(meetingId.getId())));
        //再次预定1103会议， 已预定无法再次预定
        System.out.println(commandService.reserve(reserveCommand).getReserveStatusEnum());
        //取消预定1103，不存在的预定标识无法取消
        System.out.println(commandService.cancel(new MettingCancelCommand(
                meetingId.getId(), reseveStatus.getReserveFlag() + "3", userId
        )));
        //取消预定1103，成功
        System.out.println(commandService.cancel(new MettingCancelCommand(
                meetingId.getId(), reseveStatus.getReserveFlag(), userId
        )));
        //获取1103会议室预定情况
        System.out.println(commandService.get(new MettingGetCommand(meetingId.getId())));

        MettingReserveCommand reserveCommand2 = new MettingReserveCommand(
                //预定 10点到 24点会议(大等于10小于23:59)
                meetingId.getId(), userId,
                new MettingTime(10 * 60, 24 * 60), "RBAC权限接入议题",
                meetingDate,
                mettingNumber,
                "RBAC权限接入议题",
                "http://xxxxxxxx.xlsx"
        );
        //预定1103会议室
        ReseveStatus reseveStatus2 = commandService.reserve(reserveCommand2);
        System.out.println(reseveStatus2.getReserveStatusEnum());
        //获取1103会议室预定情况
        System.out.println(commandService.get(new MettingGetCommand(meetingId.getId())));


    }

}
