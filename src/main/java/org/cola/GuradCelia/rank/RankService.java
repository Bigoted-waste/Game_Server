package org.cola.GuradCelia.rank;

import com.alibaba.fastjson.JSONObject;
import org.cola.GuradCelia.async.AsyncOperationProcessor;
import org.cola.GuradCelia.async.IAsyncOperation;
import org.cola.GuradCelia.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 排行榜服务
 */
public final class RankService {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RankService.class);
    /**
     * 单例对象
     */
    static private final RankService _instance = new RankService();

    /**
     * 私有化默认构造器
     */
    private RankService() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public RankService getInstance() {
        return _instance;
    }

    /**
     * 获取排行榜
     *
     * @param callback 回调函数
     */
    public void getRank(Function<List<RankItem>, Void> callback) {
        if (null == callback) {
            return;
        }

        AsyncOperationProcessor.getInstance().process(new AsyncGetRank() {
            @Override
            public void doFinish() {
                callback.apply(this.getRankItemList());
            }
        });
    }

    /**
     * 刷新排行榜
     *
     * @param winnerId
     * @param loserId
     */
    public void refreshRank(int winnerId, int loserId) {
        if (winnerId <= 0 || loserId <= 0) {
            return;
        }

        try (Jedis redis = RedisUtil.getJedis()) {
            redis.hincrBy("User_" + winnerId, "Win", 1);
            redis.hincrBy("User_" + loserId, "Lose", 1);

            String winStr = redis.hget("User_" + winnerId, "Win");
            int winNum = Integer.parseInt(winStr);

            redis.zadd("Rank", winNum, String.valueOf(winnerId));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    /**
     * 异步方式获取排行榜
     */
    static private class AsyncGetRank implements IAsyncOperation {

        /**
         * 排名条目列表
         */
        private List<RankItem> _rankItemList;

        /**
         * 获取排名条目列表
         *
         * @return 排名条目列表
         */
        List<RankItem> getRankItemList() {
            return _rankItemList;
        }

        @Override
        public void doAsync() {
            try (Jedis redis = RedisUtil.getJedis()) {
                // 获取集合字符串
                List<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0, 9);

                ArrayList<RankItem> rankItemList = new ArrayList<>();

                int i = 0;

                for (Tuple t : valSet) {
                    if (null == t) {
                        continue;
                    }

                    // 获取用户 Id
                    int userId = Integer.parseInt(t.getElement());

                    // 获取用户信息
                    String jsonStr = redis.hget("User_" + userId, "BasicInfo");
                    if (null == jsonStr) {
                        continue;
                    }

                    RankItem newItem = new RankItem();
                    newItem.rankId = ++i;
                    newItem.userId = userId;
                    newItem.win = (int) t.getScore();

                    JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                    newItem.userName = jsonObj.getString("userName");
                    newItem.heroAvatar = jsonObj.getString("heroAvatar");

                    rankItemList.add(newItem);
                }

                _rankItemList = rankItemList;
            } catch (Exception e) {
                // 记录错误日志
                LOGGER.error(e.getMessage(), e);
            }

        }
    }
}
