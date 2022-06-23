package org.cola.GuradCelia.async;

import org.cola.GuradCelia.MainMsgProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncOperationProcessor {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    /**
     * 单例对象
     */
    static private final AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    /**
     * 创建单线程数组
     */
    private final ExecutorService[] _esArray = new ExecutorService[8];

    /**
     * 私有化默认构造器
     */
    private AsyncOperationProcessor() {
        for (int i = 0; i < _esArray.length; i++) {
            final String threadName = "AsyncOperationProcessor[" + i + "]";
            _esArray[i] = Executors.newSingleThreadExecutor((r) -> {
                Thread t = new Thread(r);
                t.setName(threadName);
                return t;
            });
        }
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    static public AsyncOperationProcessor getInstance() {
        return _instance;
    }

    /**
     * 执行异步操作
     *
     * @param op
     */
    public void process(IAsyncOperation op) {
        if (null == op) {
            return;
        }

        int bindId = Math.abs(op.getBindId());
        int esIndex = bindId % _esArray.length;
        _esArray[esIndex].submit(() -> {
            try {
                // 执行异步操作
                op.doAsync();
                // 回到主线程执行完成逻辑
                MainMsgProcessor.getInstance().process(op::doFinish);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }
}
