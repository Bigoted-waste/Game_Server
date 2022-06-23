package org.cola.GuradCelia.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 雄安锡队列生产者
 */
public final class MqProducer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MqProducer.class);
    /**
     * 消息队列生产者
     */
    static private DefaultMQProducer _producer = null;

    /**
     * 私有化默认构造器
     */
    private MqProducer() {

    }

    /**
     * 初始化
     */
    static public void init() {
        try {
            DefaultMQProducer producer = new DefaultMQProducer("heroStory");
            producer.setNamesrvAddr("192.168.56.163:9876");
            producer.start();
            // 设置失败重试 3 次
            producer.setRetryTimesWhenSendAsyncFailed(3);

            _producer = producer;

            LOGGER.info("消息队列 ( 生产者 ) 连接成功！");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 发送消息
     *
     * @param topic 主题
     * @param msg   消息对象
     */
    static public void sendMsg(String topic, Object msg) {
        if (null == topic || null == msg){
            return;
        }

        Message newMsg = new Message();
        newMsg.setTopic(topic);
        newMsg.setBody(JSONObject.toJSONBytes(msg));
        try {
            // 发送消息
            SendResult sendResult = _producer.send(newMsg);
            LOGGER.info("producer 发送消息 --->{}",sendResult);
        } catch (Exception e) {
            // 记录日志信息
            LOGGER.error(e.getMessage(),e);
        }
    }
}
