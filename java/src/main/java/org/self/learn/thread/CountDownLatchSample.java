package org.self.learn.thread;

import java.util.concurrent.CountDownLatch;

/**
 * 闭锁: 通过AQS的共享锁来实现（状态计数）
 * <p>
 * 是一种同步的的工具类, 允许一个线程(或多个线程即多个线程内调用await)等待其他线程中执行的一组操作(sync.countDown())完成
 * <p>
 * 同样是等待线程执行,保持顺序,与join的区别:
 * <p>
 * thread.join()操作必须等待thread执行完毕，
 * ContDownDatch不必等待线程执行完毕，可以从线程的任何位置认为等待结束(通过AQS的状态countDown()来控制等待的线程数)
 * <p>
 * 等待的线程数为0时，countDownLatch.await()等待的线程将会被唤醒
 * <p>
 * CountDownLatch为一次性使用，等待线程数至0后，就结束了，无法在使用
 *
 * @date 2020-05-06.
 */
public class CountDownLatchSample {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread worker1 = new WorkerThreadA("zhangsan", "design", "coding", countDownLatch);
        Thread worker2 = new WorkerThreadB("lisi", "coding", "review", countDownLatch);
        Thread worker3 = new WorkerThreadA("wangwu", "qa test", "check", countDownLatch);
        Thread worker4 = new WorkerThreadB("zhaoliu", "online", "verify", countDownLatch);
        worker1.start();
        worker2.start();
        //等待worker1及worker2完成才进行worker3执行
        countDownLatch.await();//前主线程会进入AQS线程等待队列，直到countDown()state为0时，将会unpark唤醒
        worker3.start();
        worker4.start();
    }

    static class WorkerThreadA extends Thread {
        private String name;
        private String firstWork;
        private String secondWork;
        CountDownLatch countDownLatch;

        public WorkerThreadA(String name, String firstWork, String secondWork, CountDownLatch countDownLatch) {
            this.name = name;
            this.firstWork = firstWork;
            this.secondWork = secondWork;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            System.out.println(name + "::WorkerThreadA::do firstWork::" + firstWork);
            //等待所有worker的第一个工作完成
            countDownLatch.countDown();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(name + "::WorkerThreadA::do secondWork::" + secondWork);
        }
    }

    static class WorkerThreadB extends Thread {
        private String name;
        private String firstWork;
        private String secondWork;
        CountDownLatch countDownLatch;

        public WorkerThreadB(String name, String firstWork, String secondWork, CountDownLatch countDownLatch) {
            this.name = name;
            this.firstWork = firstWork;
            this.secondWork = secondWork;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            System.out.println(name + "::WorkerThreadB::do firstWork::" + firstWork);
            //等待所有worker的第一个工作完成
            countDownLatch.countDown();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(name + "::WorkerThreadB::do secondWork::" + secondWork);
        }
    }
}
