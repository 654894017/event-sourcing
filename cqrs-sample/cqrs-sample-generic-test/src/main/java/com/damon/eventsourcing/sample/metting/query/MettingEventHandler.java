package com.damon.eventsourcing.sample.metting.query;

//@Slf4j
//public class MettingEventHandler extends RocketMQOrderlyEventListener {
//
//    public MettingEventHandler(String nameServer, String topic, String consumerGroup, int minThread, int maxThread, int pullBatchSize, ConsumeFromWhere where) throws MQClientException {
//        super(nameServer, topic, consumerGroup, minThread, maxThread, pullBatchSize, where);
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
