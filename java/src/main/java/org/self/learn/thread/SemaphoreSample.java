package org.self.learn.thread;

import java.util.concurrent.Semaphore;

/**
 * 信号量: 限制访问的并发量
 * <p>
 * 是通过AQS的共享锁实现，信号计数器
 * <p>
 * 通过semaphore.acquire()获取信号（AQS.state+1)，当信号为0是，thread.park进入线程等待队列
 * 通过semaphore.release()释放信号（AQS.state+1)，从线程等待队列Thread.unpark(head->next node)
 *
 * @date 2020-05-06.
 */
public class SemaphoreSample {

    private static Semaphore A = new Semaphore(1);
    private static Semaphore B = new Semaphore(1);
    private static Semaphore C = new Semaphore(1);

    static class ThreadA extends Thread {

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    A.acquire();
                    System.out.println("A");
                    B.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    static class ThreadB extends Thread {

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    B.acquire();
                    System.out.println("B");
                    C.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    static class ThreadC extends Thread {

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    C.acquire();
                    System.out.println("C");
                    A.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        // 开始只有A可以获取, BC都不可以获取, 保证了A最先执行
        B.acquire();
        C.acquire();
        new ThreadA().start();
        new ThreadB().start();
        new ThreadC().start();
    }
}
