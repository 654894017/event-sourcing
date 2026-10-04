package com.damon.eventsourcing.sample.train;

import com.alibaba.fastjson.JSONObject;
import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.sample.TestConfig;
import com.damon.eventsourcing.sample.train.aggregate.value_object.TicketBuyStatus;
import com.damon.eventsourcing.sample.train.aggregate.value_object.TrainCarriage;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.SEAT_TYPE;
import com.damon.eventsourcing.sample.train.aggregate.value_object.enum_type.TICKET_BUY_STATUS;
import com.damon.eventsourcing.sample.train.command.TicketBuyCommand;
import com.damon.eventsourcing.sample.train.command.TicketProtectCommand;
import com.damon.eventsourcing.sample.train.command.TrainCreateCommand;
import com.damon.eventsourcing.sample.train.command.TrainStockGetCommand;
import com.damon.eventsourcing.sample.train.damain_service.TrainStockCommandProcessor;
import com.damon.eventsourcing.sample.train.dto.TrainStockDTO;
import com.damon.eventsourcing.utils.IdWorker;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 多场景测试
 */
public class TrainStockMultiSceneTestBootstrap {
    public static void main(String[] args) throws InterruptedException {
        EventSourcingConfig eventSourcingConfig = TestConfig.init();
        TrainStockCommandProcessor service = new TrainStockCommandProcessor(eventSourcingConfig);

        // 假设某个车次有6个站点分别为1，2，3，4，5，6。共计100个1等座位
        // 10002，100 表示站点1到站点2的票为100
        // 20003，100 表示站点2到站点3的票为100
        // 如果用户需要购买站点1到站点3的票，首先我们需要判断1到2（10002），2到3（20003）站点的余票是否大于0，如果大于0说明可以购票，然后分别扣减10002，10003分别减1。
        List<Integer> list = new ArrayList<>();
        list.add(10002);
        list.add(20003);
        list.add(30004);
        list.add(40005);
        list.add(50006);
        Long id = 202201170013L;
        TrainCreateCommand create = new TrainCreateCommand(id);
        List<TrainCarriage> trainCarriages1 = new ArrayList<>();
        trainCarriages1.add(new TrainCarriage(0, 0, 9, SEAT_TYPE.BUSINESS_CLASS));
        trainCarriages1.add(new TrainCarriage(1, 10, 19, SEAT_TYPE.BUSINESS_CLASS));
        List<TrainCarriage> trainCarriages2 = new ArrayList<>();
        trainCarriages2.add(new TrainCarriage(3, 0, 9, SEAT_TYPE.FIRST_CLASS));
        trainCarriages2.add(new TrainCarriage(4, 10, 19, SEAT_TYPE.FIRST_CLASS));
        List<TrainCarriage> trainCarriages3 = new ArrayList<>();
        trainCarriages3.add(new TrainCarriage(5, 0, 9, SEAT_TYPE.SECOND_CLASS));
        trainCarriages3.add(new TrainCarriage(6, 10, 19, SEAT_TYPE.SECOND_CLASS));
        List<TrainCarriage> trainCarriages4 = new ArrayList<>();
        trainCarriages4.add(new TrainCarriage(5, 0, 9, SEAT_TYPE.STANDING));


        create.setBusinessTrainCarriageList(trainCarriages1);
        create.setFirstTrainCarriageList(trainCarriages2);
        create.setSecondTrainCarriageList(trainCarriages3);
        create.setStandingTrainCarriageList(trainCarriages4);

        create.setStation2StationBusinessList(list);
        create.setStation2StationFirstList(list);
        create.setStation2StationSecondList(list);
        create.setStation2StationStandingList(list);
        service.createTrain(create);

        TicketProtectCommand protectCommand = new TicketProtectCommand(id);
        protectCommand.setStartStationNumber(1);
        protectCommand.setEndStationNumber(6);
        protectCommand.setMinCanBuyTicketCount(14);
        protectCommand.setMaxCanBuyTicketCount(20);
        protectCommand.setSeatType(SEAT_TYPE.BUSINESS_CLASS);
        protectCommand.setStrict(Boolean.TRUE);
        System.out.println("----------预留车票  1:6-50  -------------");
        System.out.println(service.protectTicket(protectCommand));
        LinkedBlockingQueue<Long> userIds = new LinkedBlockingQueue<>();
        System.out.println("----------开始购票 1-5 -------------");
        //购买票
        for (int i = 0; i < 1; i++) {
            TicketBuyCommand command = new TicketBuyCommand(id);
            command.setStartStationNumber(1);
            command.setEndStationNumber(6);
            command.setSeatType(SEAT_TYPE.BUSINESS_CLASS);
            Long userId = IdWorker.getId();
            command.setSeatIndexs(Lists.newArrayList(0, 1, 2, 3, 4));
            command.setUserIds(Lists.newArrayList(1L, 2l, 3l, 4l, 5l));
            TicketBuyStatus status = service.buyTicket(command);
            if (status.getStauts().equals(TICKET_BUY_STATUS.SUCCEED)) {
                userIds.add(userId);
                System.out.println("购买成功，座位号：" + status.getSeatIndexs());
            } else {
                System.err.println("购买失败，失败信息：" + status.getStauts());
            }
        }
        Thread.sleep(1000);
        getTrainStackInfo(service, id);


        //购买票
        for (int i = 0; i < 1; i++) {
            TicketBuyCommand command = new TicketBuyCommand(id);
            command.setStartStationNumber(1);
            command.setEndStationNumber(6);
            command.setSeatType(SEAT_TYPE.BUSINESS_CLASS);
            Long userId = IdWorker.getId();
            command.setSeatIndexs(Lists.newArrayList(0, 1, 2, 3, 4));
            command.setUserIds(Lists.newArrayList(6L, 7l, 8l, 9l, 10l));
            TicketBuyStatus status = service.buyTicket(command);
            if (status.getStauts().equals(TICKET_BUY_STATUS.SUCCEED)) {
                userIds.add(userId);
                System.out.println("购买成功，座位号：" + status.getSeatIndexs());
            } else {
                System.err.println("购买失败，失败信息：" + status.getStauts());
            }
        }
        Thread.sleep(1000);
        getTrainStackInfo(service, id);


        //购买票
        for (int i = 0; i < 1; i++) {
            TicketBuyCommand command = new TicketBuyCommand(id);
            command.setStartStationNumber(1);
            command.setEndStationNumber(6);
            command.setSeatType(SEAT_TYPE.BUSINESS_CLASS);
            Long userId = IdWorker.getId();
            command.setSeatIndexs(Lists.newArrayList(0, 1, 2, 3, 4));
            command.setUserIds(Lists.newArrayList(11L, 12l, 13l, 14l, 15l));
            TicketBuyStatus status = service.buyTicket(command);
            if (status.getStauts().equals(TICKET_BUY_STATUS.SUCCEED)) {
                userIds.add(userId);
                System.out.println("购买成功，座位号：" + status.getSeatIndexs());
            } else {
                System.err.println("购买失败，失败信息：" + status.getStauts());
            }
        }
        Thread.sleep(1000);
        getTrainStackInfo(service, id);


        //购买票
        for (int i = 0; i < 1; i++) {
            TicketBuyCommand command = new TicketBuyCommand(id);
            command.setStartStationNumber(1);
            command.setEndStationNumber(6);
            command.setSeatType(SEAT_TYPE.BUSINESS_CLASS);
            Long userId = IdWorker.getId();
            command.setSeatIndexs(Lists.newArrayList(0, 4));
            command.setUserIds(Lists.newArrayList(16L, 20l));
            TicketBuyStatus status = service.buyTicket(command);
            if (status.getStauts().equals(TICKET_BUY_STATUS.SUCCEED)) {
                userIds.add(userId);
                System.out.println("购买成功，座位号：" + status.getSeatIndexs());
            } else {
                System.err.println("购买失败，失败信息：" + status.getStauts());
            }
        }
        Thread.sleep(1000);
        getTrainStackInfo(service, id);


        //购买票
        for (int i = 0; i < 1; i++) {
            TicketBuyCommand command = new TicketBuyCommand(id);
            command.setStartStationNumber(1);
            command.setEndStationNumber(6);
            command.setSeatType(SEAT_TYPE.BUSINESS_CLASS);
            Long userId = IdWorker.getId();
            command.setSeatIndexs(Lists.newArrayList(0, 4));
            command.setUserIds(Lists.newArrayList(21L, 22l));
            TicketBuyStatus status = service.buyTicket(command);
            if (status.getStauts().equals(TICKET_BUY_STATUS.SUCCEED)) {
                userIds.add(userId);
                System.out.println("购买成功，座位号：" + status.getSeatIndexs());
            } else {
                System.err.println("购买失败，失败信息：" + status.getStauts());
            }
        }
        Thread.sleep(1000);
        getTrainStackInfo(service, id);


        //购买票
        for (int i = 0; i < 1; i++) {
            TicketBuyCommand command = new TicketBuyCommand(id);
            command.setStartStationNumber(1);
            command.setEndStationNumber(6);
            command.setSeatType(SEAT_TYPE.BUSINESS_CLASS);
            Long userId = IdWorker.getId();
            command.setSeatIndexs(Lists.newArrayList(1));
            command.setUserIds(Lists.newArrayList(23L));
            TicketBuyStatus status = service.buyTicket(command);
            if (status.getStauts().equals(TICKET_BUY_STATUS.SUCCEED)) {
                userIds.add(userId);
                System.out.println("购买成功，座位号：" + status.getSeatIndexs());
            } else {
                System.err.println("购买失败，失败信息：" + status.getStauts());
            }
        }
        Thread.sleep(1000);
        getTrainStackInfo(service, id);

//        TicketProtectCancelCommand cancelCommandCommand = new TicketProtectCancelCommand(IdWorker.getId(), id);
//        cancelCommandCommand.setStartStationNumber(1);
//        cancelCommandCommand.setEndStationNumber(6);
//        cancelCommandCommand.setStrict(Boolean.TRUE);
//        System.out.println("----------取消保护-------------");
//        System.out.println(service.cancelProtectTicket(cancelCommandCommand));
//
//        System.out.println("----------开始购票2 1-6 -------------");
//        //购买票
//        for (int i = 0; i < 50; i++) {
//            TicketBuyCommand command = new TicketBuyCommand(IdWorker.getId(), id);
//            command.setStartStationNumber(1);
//            command.setEndStationNumber(6);
//            Long userId = IdWorker.getId();
//            command.setUserId(userId);
//            TicketBuyStatus status = service.buyTicket(command);
//            if (status.getStauts().equals(TICKET_BUY_STATUS.SUCCEED)) {
//                userIds.add(userId);
//                System.out.println("购买成功，座位号：" + status.getSeatIndex());
//            } else {
//                System.err.println("购买失败，失败信息：" + status.getStauts());
//            }
//        }
//        Thread.sleep(1000);
//        getTrainStackInfo(service, id);
//
//        System.out.println("----------取消购票-------------");
//
//        //取消购票
//        for (int i = 0; i < 10; i++) {
//            TicketCancelCommand command = new TicketCancelCommand(IdWorker.getId(), id);
//            command.setStartStationNumber(1);
//            command.setEndStationNumber(6);
//            command.setUserId(userIds.take());
//            System.out.println(service.cancelTicket(command));
//        }
//        Thread.sleep(1000);
//        getTrainStackInfo(service, id);
//        System.out.println("----------开始购票3 1-6 -------------");
//        //购买票
//        for (int i = 0; i < 11; i++) {
//            TicketBuyCommand command = new TicketBuyCommand(IdWorker.getId(), id);
//            command.setStartStationNumber(1);
//            command.setEndStationNumber(5);
//            command.setSeatType(SEAT_TYPE.FIRST_CLASS);
//            Long userId = IdWorker.getId();
//            command.setUserId(userId);
//            TicketBuyStatus status = service.buyTicket(command);
//            if (status.getStauts().equals(TICKET_BUY_STATUS.SUCCEED)) {
//                userIds.add(userId);
//                System.out.println("购买成功，座位号：" + status.getSeatIndex());
//            } else {
//                System.err.println("购买失败，失败信息：" + status.getStauts());
//            }
//        }
//        Thread.sleep(1000);
//        getTrainStackInfo(service, id);


//        System.out.println("----------开始购票4 5-6 -------------");
//        //购买票
//        for (int i = 0; i < 30; i++) {
//            TicketBuyCommand command = new TicketBuyCommand(IdWorker.getId(), id);
//            command.setStartStationNumber(5);
//            command.setEndStationNumber(6);
//            Long userId = IdWorker.getId();
//            command.setUserId(userId);
//            TrainStock.TicketBuyStatus status = service.buyTicket(command);
//            if (status.getStauts().equals(TrainStock.TICKET_BUY_STAUTS.SUCCEED)) {
//                userIds.add(userId);
//                System.out.println("购买成功，座位号：" + status.getSeatIndex());
//            } else {
//                System.err.println("购买失败，失败信息：" + status.getStauts());
//            }
//        }
//        Thread.sleep(1000);
//        getTrainStackInfo(service, id);

//        TicketProtectCommand protectCommand2 = new TicketProtectCommand(IdWorker.getId(), id);
//        protectCommand2.setStartStationNumber(5);
//        protectCommand2.setEndStationNumber(6);
//        protectCommand2.setMinCanBuyTicketCount(1);
//        protectCommand2.setMaxCanBuyTicketCount(2);
//        protectCommand2.setStrict(Boolean.TRUE);
//        System.out.println("----------预留车票  5:6-5  -------------");
//        System.out.println(service.protectTicket(protectCommand2));
//        Thread.sleep(1000);
//        getTrainStackInfo(service, id);
//
//        //购买票
//        for (int i = 0; i < 3; i++) {
//            TicketBuyCommand command = new TicketBuyCommand(IdWorker.getId(), id);
//            command.setStartStationNumber(5);
//            command.setEndStationNumber(6);
//            Long userId = IdWorker.getId();
//            command.setUserId(userId);
//            TicketBuyStatus status = service.buyTicket(command);
//            if (status.getStauts().equals(TICKET_BUY_STATUS.SUCCEED)) {
//                userIds.add(userId);
//                System.out.println("购买成功，座位号：" + status.getSeatIndex());
//            } else {
//                System.err.println("购买失败，失败信息：" + status.getStauts());
//            }
//        }
//        Thread.sleep(1000);
//        getTrainStackInfo(service, id);


    }

    public static void getTrainStackInfo(TrainStockCommandProcessor service, Long id) {
        //获取车次信息（包含座位信息）
        TrainStockGetCommand command = new TrainStockGetCommand(IdWorker.getId(), id);

        TrainStockDTO trainStock = service.getTrain(command);

        System.out.println(JSONObject.toJSONString(trainStock));
    }

}

