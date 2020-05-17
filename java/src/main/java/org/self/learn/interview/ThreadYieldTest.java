package org.self.learn.interview;

import java.util.function.Supplier;

/**
 * 线程让步, 让当前线程让出执行权，让其他可运行的线程得以执行，
 * 但并不是其他可运行的线程就一定被执行，只是获得更高的执行机会
 *
 * @date 2020-04-30.
 */
public class ThreadYieldTest {

    static Supplier<String> spplier;

    public static void main(String[] args) {
        Thread t1 = new MyThread1();
        Thread t2 = new Thread(new MyRunnable());

        t2.start();
        t1.start();
    }

    static class MyThread1 extends Thread {
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.println("线程1第" + i + "次执行！");
                //测试是否切回之前yield的线程MyRunnable
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class MyRunnable implements Runnable {
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.println("线程2第" + i + "次执行！");
                Thread.yield();
            }
        }
    }
}
