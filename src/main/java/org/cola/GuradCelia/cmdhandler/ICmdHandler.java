package org.cola.GuradCelia.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理命令接口
 * @param <TCmd>
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    /**
     * 处理命令
     * @param ctx
     * @param msg
     */
    void handle(ChannelHandlerContext ctx, TCmd msg);
}
