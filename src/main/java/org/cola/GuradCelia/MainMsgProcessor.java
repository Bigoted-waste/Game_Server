package org.cola.GuradCelia;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.cola.GuradCelia.cmdhandler.CmdHandlerFactory;
import org.cola.GuradCelia.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主消息处理器
 * MainMsgProcessor 解决主业务逻辑
 * AsyncOperationProcessor 解决 IO 数据库中的业务逻辑
 */
public final class MainMsgProcessor {

    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MainMsgProcessor.class);

    /**
     * 单列对象
     */
    static private final MainMsgProcessor _instance = new MainMsgProcessor();

    /**
     * 私有化类默认构造器
     */
    private MainMsgProcessor() {
    }

    /**
     * 创建一个单线程的线程池
     */
    private final ExecutorService _es = Executors.newSingleThreadExecutor((newRunnable) -> {
        Thread newThread = new Thread(newRunnable);
        newThread.setName("MainMsgProcessor");
        return newThread;
    });

    /**
     * 获取单列对象
     *
     * @return 单列对象
     */
    static public MainMsgProcessor getInstance() {
        return _instance;
    }

    public void process(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }
        LOGGER.info("收到客户端消息，msgClazz = {} , msgBody = {}",
                msg.getClass().getSimpleName(),
                msg
        );

        _es.submit(() -> {
            try {
                ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());
                if (null != cmdHandler) {
                    cmdHandler.handle(ctx, cast(msg));
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }

    /**
     * 处理 Runnable 实例
     * @param r
     */
    public void process(Runnable r){
        if (null == r){
            return;
        }

        _es.submit(r);
    }

    /**
     * 转型为命令对象
     *
     * @param msg    消息对象
     * @param <TCmd> 消息类型
     * @return
     */
    @SuppressWarnings("unchecked")
    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (null == msg) {
            return null;
        } else {
            return (TCmd) msg;
        }
    }
}
