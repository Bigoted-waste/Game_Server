package org.cola.GuradCelia.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.cola.GuradCelia.rank.RankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 消息队列消费者
 */
public final class MqConsumer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MqProducer.class);

    /**
     * 私有化默认对象
     */
    private MqConsumer() {

    }

    /**
     * 初始化
     */
    static public void init() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("herostory");
        consumer.setNamesrvAddr("192.168.56.163:9876");

        try {
            consumer.subscribe("herostory_victor", "*");

            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgExtList, ConsumeConcurrentlyContext ctx) {
                    for (MessageExt msgExt : msgExtList) {
                        VictorMsg victorMsg = JSONObject.parseObject(
                                msgExt.getBody(),
                                VictorMsg.class
                        );
                        LOGGER.info(
                                "从消息队列中收到胜利消息,winnerId = {} , LoserId = {}",
                                victorMsg.winnerId,
                                victorMsg.loserId
                        );
                        RankService.getInstance().refreshRank(victorMsg.winnerId,victorMsg.loserId);
                    }

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            consumer.start();
            LOGGER.info("消息队列 ( 消费者 ) 连接成功!");
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }

    }
}
