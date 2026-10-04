package com.damon.eventsourcing.event_store;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(5);
        new Thread(() -> {
            List<Integer> list = new ArrayList<Integer>();
            System.out.println(1);
            for (int i = 0; i < 1000000; i++) {
                list.add(i);
            }
            try {
                Thread.sleep(10000000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


    }
}
