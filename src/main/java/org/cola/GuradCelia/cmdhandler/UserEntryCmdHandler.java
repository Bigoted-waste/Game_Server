package org.cola.GuradCelia.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.cola.GuradCelia.Broadcaster;
import org.cola.GuradCelia.model.User;
import org.cola.GuradCelia.model.UserManager;
import org.cola.GuradCelia.msg.GameMsgProtocol;

/**
 * 用户进入游戏
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd>{

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd){
        if (null == ctx || null == cmd){
            return;
        }

        // 获取用户 Id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (null == userId){
            return;
        }

        User existUser = UserManager.getByUserId(userId);
        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setUserName(existUser.userName);
        resultBuilder.setHeroAvatar(existUser.heroAvatar);

        // 构建结构并广播
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
