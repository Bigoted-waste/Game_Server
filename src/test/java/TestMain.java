public class TestMain {

    // 解决血量在高并发下所出现的问题
    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            System.out.println("第"+ i +"次测试");
            (new TestMain()).test3();
        }
    }


    private void test1() {
        TestUser newUser = new TestUser();
        newUser.currMp = 100;

        Thread t1 = new Thread(() -> {
            newUser.currMp = newUser.currMp - 1;
        });

        Thread t2 = new Thread(() -> {
            newUser.currMp = newUser.currMp - 1;
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(newUser.currMp != 98){
            throw new RuntimeException("当前血量错误 ， currHp ="+newUser.currMp);
        }else {
            System.out.println("当前血量正确");
        }
    }

    private void test2() {
        TestUser newUser = new TestUser();
        newUser.currMp = 100;

        Thread t1 = new Thread(() -> {
            newUser.subtractHp(1);
        });

        Thread t2 = new Thread(() -> {
            newUser.subtractHp(1);
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(newUser.currMp != 98){
            throw new RuntimeException("当前血量错误 ， currHp ="+newUser.currMp);
        }else {
            System.out.println("当前血量正确");
        }
    }

    private void test3() {
        TestUser user1 = new TestUser();
        user1.currMp = 100;

        TestUser user2 = new TestUser();
        user2.currMp = 100;

        Thread t1 = new Thread(() -> {
            user1.attUser(user2);
        });

        Thread t2 = new Thread(() -> {
            user2.attUser(user1);
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
