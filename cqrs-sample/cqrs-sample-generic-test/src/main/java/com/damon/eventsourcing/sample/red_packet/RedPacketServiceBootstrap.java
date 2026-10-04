package com.damon.eventsourcing.sample.red_packet;

import com.damon.eventsourcing.config.EventSourcingConfig;
import com.damon.eventsourcing.sample.TestConfig;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketCreateCommand;
import com.damon.eventsourcing.sample.red_packet.api.command.RedPacketGrabCommand;
import com.damon.eventsourcing.sample.red_packet.domain.service.RedPacketCommandProcessor;
import com.damon.eventsourcing.utils.IdWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RedPacketServiceBootstrap {
    public static void main(String[] args) throws InterruptedException {
        EventSourcingConfig eventSourcingConfig = TestConfig.init();
        RedPacketCommandProcessor redPacketServcie = new RedPacketCommandProcessor(eventSourcingConfig);
        List<Long> ids = new ArrayList<>();
        for (int i = 1; i <= 2000; i++) {
            Long id = IdWorker.getId();
            RedPacketCreateCommand create = new RedPacketCreateCommand(id);
            create.setMoney(new BigDecimal(20000));
            create.setNumber(new BigDecimal(10000));
            create.setMinMoney(new BigDecimal(1));
            create.setSponsorId(1L);
            redPacketServcie.createRedPackage(create);
            ids.add(id);
        }
        Random random = new Random();
        CountDownLatch latch = new CountDownLatch(2 * 2000 * 1000);
        int size = ids.size();
        ExecutorService service = Executors.newFixedThreadPool(1000);
        Long startDate = System.currentTimeMillis();
        System.out.println("start");
        for (int i = 0; i < 500; i++) {
            service.submit(() -> {
                for (int number = 0; number < 300000; number++) {
                    try {
                        int index = random.nextInt(size);
                        Long id = ids.get(index);
                        RedPacketGrabCommand grabCommand = new RedPacketGrabCommand(id);
                        grabCommand.setUserId(IdWorker.getId());
                        int status = redPacketServcie.grabRedPackage(grabCommand);
                        if (status <= 0) {
                            System.out.println("failed");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        latch.await();
        Long endDate = System.currentTimeMillis();
        System.out.println(endDate - startDate);

    }

}

