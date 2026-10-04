package com.damon.eventsourcing.sample.red_packet.query.event_handler;

/// **
// * 红包事件监听器
// *
// * @author xianpinglu
// */
//@Slf4j
//public class RedPacketEventListener extends RocketMQOrderlyEventListener {
//
//    public RedPacketEventListener(String nameServer, String topic, String consumerGroup, int minThread, int maxThread, int pullBatchSize) throws MQClientException {
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
