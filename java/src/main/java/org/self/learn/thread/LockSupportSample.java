package org.self.learn.thread;

import java.util.ArrayList;
import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport基于许可证线程通信
 * <p>
 * 锁的创建于同步管理，通过permit许可来休眠当前线程(WAITING)或者唤醒当前线程，可替换基于对象监视器wait/notify通信
 * <p>
 * LockSupport相对于对象的wait/notify方法的优点:
 * <p>
 * park/unpark更加简单和灵活: 可以指定为具体的线程通过许可证来唤醒，
 * <p>
 * 其次park/unpark没有顺序性，先执行unpark后，再执行park不会造成阻塞
 * <p>
 * 线程间通信不用依赖对象监视器，实现线程的解耦
 *
 * @date 2020-05-05.
 */
public class LockSupportSample {
    public static void main(String[] args) throws InterruptedException {

        //1: 验证park与unpark的无顺序性，不会导致先得到许可证后，然后未得到许可证的执行
        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                for (int i = 0; i < 10; i++) {
                    sum += i;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LockSupport.park();
                System.out.println(Thread.currentThread().getName() + " sum = " + sum);
            }
        }, "ThreadA");
        threadA.start();

        Thread.sleep(1000);

        System.out.println(threadA.getState());


        Thread.sleep(10000);


        //2: 按需将队列里的线程进行授权执行unpark, AQS队列里的线程tryRelease时要执行的操作
        ArrayList<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    LockSupport.park();
                    System.out.println(Thread.currentThread().getName() + "==" + finalI);
                }
            });
            threadList.add(thread);
        }
        for (Thread thread : threadList) {
            thread.start();
            LockSupport.unpark(thread);
        }
    }

    //LockSupport不支持重入，只能有一次permit生效
    static class Temp {
        public static void main(String[] args) {
            Thread thread = Thread.currentThread();

            LockSupport.unpark(thread);
            LockSupport.unpark(thread);

            System.out.println("a");
            LockSupport.park();
            System.out.println("b");
            LockSupport.park();
            System.out.println("c");
        }
    }
}
