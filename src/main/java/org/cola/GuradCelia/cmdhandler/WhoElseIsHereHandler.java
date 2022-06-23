package org.cola.GuradCelia.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.cola.GuradCelia.model.User;
import org.cola.GuradCelia.model.UserManager;
import org.cola.GuradCelia.msg.GameMsgProtocol;

import java.util.Collection;

/**
 * 还有谁在场
 */
public class WhoElseIsHereHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd>{

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        if(null == ctx || null == cmd){
            return;
        }
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        // 用户列表
        Collection<User> userList = UserManager.listUser();
        for (User currUser : userList) {
            if (null == currUser) {
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder
                    = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();

            userInfoBuilder.setUserId(currUser.userId);
            userInfoBuilder.setHeroAvatar(currUser.heroAvatar);

            // 构建移动状态
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder
                    mvStateBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            mvStateBuilder.setFromPosX(currUser.moveState.fromPosX);
            mvStateBuilder.setFromPosY(currUser.moveState.fromPosY);
            mvStateBuilder.setToPosX(currUser.moveState.toPosX);
            mvStateBuilder.setToPosY(currUser.moveState.toPosY);
            mvStateBuilder.setStartTime(currUser.moveState.startTime);
            userInfoBuilder.setMoveState(mvStateBuilder);

            resultBuilder.addUserInfo(userInfoBuilder);
        }

        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
