package org.self.learn.thread;

/**
 * 线程同步.
 *
 * @date 2020-05-01.
 */
public class ThreadSync {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 1000000; i++) {
                synchronized (Counter.lock) {
                    Counter.increase();
                }
            }
        });
        thread.start();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000000; i++) {
                synchronized (Counter.lock) {
                    Counter.decrease();
                }
            }
        });
        thread1.start();

        //等待上面两个线程执行完毕
        thread.join();
        thread1.join();

        System.out.println(Counter.count);
    }

    static class Counter {

        //或者直接在方法上加上synchronized
        private static final Object lock = new Object();

        static private int count = 0;

        static void increase() {
            count++;
        }

        static void decrease() {
            count--;
        }
    }
}
