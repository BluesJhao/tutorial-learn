package org.self.learn.thread;

/**
 * 死锁:
 * <p>
 * 多个线程持有各自占有锁后，在没有释放的情况下，尝试去获取对方的占用的锁，造成无限期等待的过程
 * <p>
 * 如果造成死锁, 需要将相互抢占资源改为顺序访问资源.
 *
 * @date 2020-05-02.
 */
public class DeadLockSample {

    public static void main(String[] args) {
        Factory factory = new Factory();
        for (int i = 0; i < 1; i++) {
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(factory.createCar());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(factory.createFood());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread2.start();
            thread1.start();
        }
    }

    static class Factory {
        private final Object lock1 = new Object();
        private final Object lock2 = new Object();

        String createCar() throws InterruptedException {
            System.out.println("Waiting createCar");
            synchronized (lock2) {
                System.out.println("entry createCar");
                Thread.sleep(5000);
                synchronized (lock1) {
                    return "created car";
                }
            }
        }

        String createFood() throws InterruptedException {
            System.out.println("Waiting createFood");
            synchronized (lock1) {
                System.out.println("entry createFood");
                Thread.sleep(2000);
                synchronized (lock2) {
                    return "created food";
                }
            }
        }
    }
}
