import java.util.concurrent.LinkedBlockingQueue;

public class TestBlockingQueue {
    public static void main(String[] args) {
        new TestBlockingQueue().test1();
    }

    public void test1() {
        LinkedBlockingQueue<Object> blockingQueue = new LinkedBlockingQueue<>();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                blockingQueue.offer(i);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 10; i < 20; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                blockingQueue.offer(i);
            }
        });

        Thread t3 = new Thread(() -> {
            try {
                while (true) {
                    Integer val = (Integer) blockingQueue.take();
                    System.out.println("获取数值 = " + val);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
