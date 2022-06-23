package org.cola.GuradCelia.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.cola.GuradCelia.Broadcaster;
import org.cola.GuradCelia.model.User;
import org.cola.GuradCelia.model.UserManager;
import org.cola.GuradCelia.mq.MqProducer;
import org.cola.GuradCelia.mq.VictorMsg;
import org.cola.GuradCelia.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户攻击命令处理器
 */
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    /**
     * 日志对象
     */
    static final Logger LOGGER = LoggerFactory.getLogger(UserAttkCmdHandler.class);


    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }

        // 获取攻击用户Id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (null == attkUserId) {
            return;
        }

        // 获取目标用户id
        int targetUserId = cmd.getTargetUserId();
        // 获取目标用户
        User targetUser = UserManager.getByUserId(targetUserId);

        if (null == targetUser) {
            broadcastAttkResult(attkUserId, -1);
            return;
        }

        LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

        // 攻击 -10
        final int dmgPoint = 10;
        targetUser.currHp = targetUser.currHp - dmgPoint;

        // 广播攻击结果
        broadcastAttkResult(attkUserId, targetUserId);
        // 广播减血结果
        broadcastSubtractHpResult(targetUserId, dmgPoint);

        if (targetUser.currHp <= 0) {
            // 广播死亡结果
            broadcastDieResult(targetUserId);

            VictorMsg newMsg = new VictorMsg();
            newMsg.winnerId = attkUserId;
            newMsg.loserId = targetUserId;
            MqProducer.sendMsg("herostory_victor",newMsg);
        }
    }

    /**
     * 广播攻击
     *
     * @param attkUserId
     * @param targetUserId
     */
    static private void broadcastAttkResult(int attkUserId, int targetUserId) {
        if (attkUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }

    /**
     * 广播见血结果
     *
     * @param targetUserId
     * @param subtractHp
     */
    static private void broadcastSubtractHpResult(int targetUserId, int subtractHp) {
        if (targetUserId <= 0 || subtractHp <= 0) {
            return;
        }

        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(subtractHp);

        GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }

    /**
     * 广播死亡结果
     *
     * @param targetUserId
     */
    static private void broadcastDieResult(int targetUserId) {
        if (targetUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserDieResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
