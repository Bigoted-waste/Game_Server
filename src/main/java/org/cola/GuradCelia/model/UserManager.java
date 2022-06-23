package org.cola.GuradCelia.model;

import org.cola.GuradCelia.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    /**
     * 用户字典
     */
    static private final Map<Integer, User> _userMap = new ConcurrentHashMap<>();

    /**
     * 私有化类默认构造器
     */
    private UserManager(){

    }

    /**
     * 添加用户
     * @param u
     */
    static public void addUser(User u){
        if (null != u) {
            _userMap.putIfAbsent(u.userId, u);
        }
    }

    /**
     * 移除用户
     * @param id
     */
    static public void removeUserByUserId(int id){
        _userMap.remove(id);
    }

    /**
     * 列表用户
     * @return
     */
    static public Collection<User> listUser(){
        return _userMap.values();
    }

    /**
     * 根据用户 Id 获取用户
     * @param userId
     * @return
     */
    static public User getByUserId(int userId){
        return _userMap.get(userId);
    }
}
