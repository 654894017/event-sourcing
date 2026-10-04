package com.damon.eventsourcing.sample.train.listener;

/**
 * Q端车次事件监听器，更新Q端数据
 *
 * @author xianpinglu
 */
//@Slf4j
//public class TrainEventListener extends RocketMQOrderlyEventListener {
//
//    public TrainEventListener(String nameServer, String topic, String consumerGroup, int minThread, int maxThread, int pullBatchSize) throws MQClientException {
//        super(nameServer, topic, consumerGroup, minThread, maxThread, pullBatchSize);
//    }
//
//    @Override
//    public void process(List<List<Event>> events) {
//
//        events.forEach(event -> {
//            log.info(JSONObject.toJSONString(event));
//        });
//
//    }
//}
