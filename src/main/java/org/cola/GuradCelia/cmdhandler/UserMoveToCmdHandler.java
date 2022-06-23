package org.cola.GuradCelia.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.cola.GuradCelia.Broadcaster;
import org.cola.GuradCelia.model.User;
import org.cola.GuradCelia.model.UserManager;
import org.cola.GuradCelia.msg.GameMsgProtocol;

/**
 * 用户移动
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd>{

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        if(null == ctx || null == cmd){
            return;
        }
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if(null == userId){
            return;
        }

        // 获取已有用户
        User existUser = UserManager.getByUserId(userId);

        if(null == existUser){
            return;
        }

        long nowTime = System.currentTimeMillis();

        existUser.moveState.fromPosX = cmd.getMoveFromPosX();
        existUser.moveState.fromPosY = cmd.getMoveFromPosY();
        existUser.moveState.toPosX = cmd.getMoveToPosX();
        existUser.moveState.toPosY = cmd.getMoveToPosY();
        existUser.moveState.startTime = nowTime;

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(cmd.getMoveFromPosX());
        resultBuilder.setMoveFromPosY(cmd.getMoveFromPosY());
        resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
        resultBuilder.setMoveToPosY(cmd.getMoveToPosX());
        resultBuilder.setMoveStartTime(System.currentTimeMillis());

        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
