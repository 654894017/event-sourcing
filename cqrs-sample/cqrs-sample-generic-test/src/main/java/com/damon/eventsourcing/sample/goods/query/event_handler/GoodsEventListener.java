package com.damon.eventsourcing.sample.goods.query.event_handler;

/**
 * goods事件监听器
 *
 * @author xianpinglu
 */
//@Slf4j
//public class GoodsEventListener extends KafkaEventOrderlyListener {
//
//    public GoodsEventListener(KafkaConsumerConfig consumerConfig) {
//        super(consumerConfig);
//    }
//
//    @Override
//    public void process(List<List<Event>> events) {
//        events.forEach(eventList -> {
//            System.out.println(Thread.currentThread().getName() + ":" + JSONObject.toJSONString(eventList));
//        });
//    }
//}
