package org.cola.GuradCelia;

import org.cola.GuradCelia.mq.MqConsumer;
import org.cola.GuradCelia.util.RedisUtil;

public class RankApp {
    public static void main(String[] args) {
        RedisUtil.init();
        MqConsumer.init();
    }
}
