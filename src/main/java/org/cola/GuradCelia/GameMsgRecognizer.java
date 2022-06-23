package org.cola.GuradCelia;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.cola.GuradCelia.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 */
public class GameMsgRecognizer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    /**
     * 消息编号 -> 消息对象字典
     */
    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgObjMap = new HashMap<>();

    /**
     * 消息类 -> 消息编号
     */
    static private final Map<Class<?> , Integer> _msgClazzAndMsgCodeMap = new HashMap<>();

    /**
     * 私有化类默认构造器
     */
    private GameMsgRecognizer() {

    }

    /**
     * 初始化
     */
    static public void init() {
//        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE, GameMsgProtocol.UserEntryCmd.getDefaultInstance());
//        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE, GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
//        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE, GameMsgProtocol.UserMoveToCmd.getDefaultInstance());
//
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserEntryResult.class, GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.WhoElseIsHereResult.class, GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserMoveToResult.class, GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserQuitResult.class, GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);

        LOGGER.info("========= 完成消息类与消息编号映射 =============");

        // 获取内部类
        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();
        for (Class<?> innerClazz : innerClazzArray) {
            if (null == innerClazz || !(GeneratedMessageV3.class.isAssignableFrom(innerClazz))){
                // 如果不是消息类
                continue;
            }

            // 获取类名称并小写
            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();
            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                if(null == msgCode){
                    continue;
                }

                // 获取消息编码
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_","");
                strMsgCode = strMsgCode.toLowerCase();

                if(!strMsgCode.startsWith(clazzName)){
                    continue;
                }
                try {
                    // 相当于调用 UserEntryCmd.getDefaultInstance();
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);

                    LOGGER.info(" {} <===> {} ",innerClazz.getName(),msgCode.getNumber());

                    _msgCodeAndMsgObjMap.put(msgCode.getNumber(),(GeneratedMessageV3) returnObj);

                    _msgClazzAndMsgCodeMap.put(innerClazz,msgCode.getNumber());
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(),e);
                }

            }
        }

    }

    static public Message.Builder getBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }

        GeneratedMessageV3 defaultMsg = _msgCodeAndMsgObjMap.get(msgCode);
        if (null == defaultMsg) {
            return null;
        } else {
            return defaultMsg.newBuilderForType();
        }
    }

    /**
     * 根据消息类获取消息编号
     * @param msgClazz
     * @return
     */
    static public int getMsgCodeByClazz(Class<?> msgClazz){
        if (null == msgClazz){
            return -1;
        }

        Integer msgCode = _msgClazzAndMsgCodeMap.get(msgClazz);

        if (null == msgClazz){
            return -1;
        }else {
            return msgCode.intValue();
        }
    }
}
