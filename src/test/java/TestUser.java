/**
 * 测试用户
 */
public class TestUser {
    /**
     * 当前血量
     */
    public int currMp;

    /**
     * 减血
     *
     * @param val
     */
    synchronized public void subtractHp(int val){
        if (currMp <= 0){
            return;
        }
        this.currMp = this.currMp - val;
    }

    /**
     * 攻击
     *
     * @param targetUser
     */
    public void attUser(TestUser targetUser){
        if (null == targetUser){
            return;
        }

        synchronized (this){
            final int dmgPoint = 10;
            targetUser.subtractHp(dmgPoint);
        }
    }
}
