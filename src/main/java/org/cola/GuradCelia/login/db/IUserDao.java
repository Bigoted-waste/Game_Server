package org.cola.GuradCelia.login.db;

public interface IUserDao {
    /**
     * 根据用户名称获取实体
     *
     * @param userName  用户名称
     * @return
     */
    UserEntity getByUserName(String userName);


    /**
     * 添加用户实体
     *
     * @param userEntity    用户实体
     */
    void insertInto(UserEntity userEntity);
}
