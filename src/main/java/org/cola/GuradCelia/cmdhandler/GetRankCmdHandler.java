package org.cola.GuradCelia.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.cola.GuradCelia.msg.GameMsgProtocol;
import org.cola.GuradCelia.rank.RankItem;
import org.cola.GuradCelia.rank.RankService;

import java.util.Collections;

/**
 * 获取排行榜指令处理器
 */
public class GetRankCmdHandler implements ICmdHandler<GameMsgProtocol.GetRankCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }

        RankService.getInstance().getRank((rankItemList) -> {
            if (null == rankItemList){
                rankItemList = Collections.emptyList();
            }

            GameMsgProtocol.GetRankResult.Builder resultBuilder = GameMsgProtocol.GetRankResult.newBuilder();

            for (RankItem rankItem :rankItemList){
                if (null == rankItem){
                    continue;
                }

                GameMsgProtocol.GetRankResult.RankItem.Builder rankItemBuilder = GameMsgProtocol.GetRankResult.RankItem.newBuilder();
                rankItemBuilder.setRankId(rankItem.rankId);
                rankItemBuilder.setUserId(rankItem.userId);
                rankItemBuilder.setUserName(rankItem.userName);
                rankItemBuilder.setHeroAvatar(rankItem.heroAvatar);
                rankItemBuilder.setWin(rankItem.win);

                resultBuilder.addRankItem(rankItemBuilder);
            }

            GameMsgProtocol.GetRankResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);
            return null;
        });

    }
}
