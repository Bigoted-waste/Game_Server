package org.cola.GuradCelia.login;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.cola.GuradCelia.MySqlSessionFactory;
import org.cola.GuradCelia.async.AsyncOperationProcessor;
import org.cola.GuradCelia.async.IAsyncOperation;
import org.cola.GuradCelia.login.db.IUserDao;
import org.cola.GuradCelia.login.db.UserEntity;
import org.cola.GuradCelia.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

/**
 * 登录服务
 */
public final class LoginService {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    /**
     * 单列对象
     */
    static private final LoginService _instance = new LoginService();

    /**
     * 私有化类默认构造器
     */
    private LoginService() {
    }

    /**
     * 获取单列对象
     *
     * @return
     */
    static public LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登录
     *
     * @param userName
     * @param password
     * @param callback
     */
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (null == userName || null == password) {
            return;
        }

        AsyncGetUserEntity asyncOp = new AsyncGetUserEntity(userName, password) {
            @Override
            public int getBindId() {
                return userName.charAt(userName.length() - 1);
            }

            @Override
            public void doFinish() {
                if (null != callback) {
                    callback.apply(this.getUserEntity());
                }
            }
        };
        AsyncOperationProcessor.getInstance().process(asyncOp);
    }

    /**
     * 更新 Redis 中的用户基本信息
     *
     * @param userEntity 用户实体
     */
    private void updateBasicInfoInRedis(UserEntity userEntity){
        if (null == userEntity){
            return;
        }

        try(Jedis redis = RedisUtil.getJedis()) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("userName",userEntity.userName);
            jsonObj.put("heroAvatar",userEntity.heroAvatar);

            redis.hset("User_"+userEntity.userId,"BasicInfo",jsonObj.toJSONString());
        }catch (Exception e){
            // 记录错误日志
            LOGGER.error(e.getMessage(),e);
        }
    }

    /**
     * 异步方式获取用户实体
     */
    private class AsyncGetUserEntity implements IAsyncOperation {

        /**
         * 用户名称
         */
        private final String _userName;

        /**
         * 密码
         */
        private final String _password;

        /**
         * 用户实例
         */
        private UserEntity _userEntity;

        /**
         * 类型构造器
         *
         * @param userName
         * @param password
         */
        AsyncGetUserEntity(String userName, String password) {
            _userName = userName;
            _password = password;
        }

        public UserEntity getUserEntity() {
            return _userEntity;
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
                // 获取 DAO
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);
                // 获取用户实体
                UserEntity userEntity = dao.getByUserName(_userName);

                LOGGER.info("当前线程 = {}", Thread.currentThread().getName());
                if (null != userEntity) {
                    if (!_password.equals(userEntity.password)) {
                        throw new RuntimeException("密码错误");
                    }
                } else {
                    userEntity = new UserEntity();
                    userEntity.userName = _userName;
                    userEntity.password = _password;
                    userEntity.heroAvatar = "Hero_Shaman";

                    dao.insertInto(userEntity);
                }

                LoginService.getInstance().updateBasicInfoInRedis(userEntity);
                _userEntity = userEntity;

//                if (null != callback) {
//                    callback.apply(userEntity);
//                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
