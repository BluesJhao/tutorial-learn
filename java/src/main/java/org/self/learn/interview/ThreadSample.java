package org.self.learn.interview;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * comment
 *
 * @date 2019-10-28.
 */
public class ThreadSample {

    public class Task implements Runnable {

        String prt;

        Task(String prt) {
            this.prt = prt;
        }

        @Override
        public void run() {
            System.out.println(prt);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadSample threadSample = new ThreadSample();
        for (int i = 0; i < 1; i++) {
            Thread a = new Thread(threadSample.new Task("A"));
            Thread b = new Thread(threadSample.new Task("B"));
            Thread c = new Thread(threadSample.new Task("C"));
            System.out.println("12");
            a.start();

            a.join();
            System.out.println("123");
            b.start();
            a.join();

            c.start();
            a.join();


            System.out.println("========================");
        }
    }

    public static class SeeDoctorTask implements Runnable {

        private CountDownLatch countDownLatch;

        public SeeDoctorTask(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                //                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName() + "看大夫成功，大夫给开了些药单子");
            } finally {
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
        }
    }

    public static class QueueTask implements Runnable {

        private CountDownLatch countDownLatch;

        public QueueTask(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + "排队成功，可以开始交费");
            } finally {
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
        }
    }

    //    public static void main(String[] args) throws InterruptedException {
    //        long now = System.currentTimeMillis();
    //        CountDownLatchSample countDownLatch = new CountDownLatchSample(10);
    //        Executor executor = Executors.newFixedThreadPool(2);
    //        executor.execute(new QueueTask(countDownLatch));
    //        executor.execute(new QueueTask(countDownLatch));
    //        executor.execute(new QueueTask(countDownLatch));
    //        executor.execute(new QueueTask(countDownLatch));
    //        executor.execute(new QueueTask(countDownLatch));
    //        executor.execute(new SeeDoctorTask(countDownLatch));
    //        executor.execute(new SeeDoctorTask(countDownLatch));
    //        executor.execute(new SeeDoctorTask(countDownLatch));
    //        executor.execute(new SeeDoctorTask(countDownLatch));
    //        executor.execute(new SeeDoctorTask(countDownLatch));
    //        countDownLatch.await();
    //        System.out.println("over，回家 cost:"+(System.currentTimeMillis()-now));
    //        executor.execute(new SeeDoctorTask(countDownLatch));
    //        int threadNum = 10;
    //        CountDownLatchSample latch = new CountDownLatchSample(1);
    //
    //        for(int i = 0; i < threadNum; i++) {
    //            TaskThread task = new TaskThread(latch);
    //            task.start();
    //        }
    //
    //        Thread.sleep(2000);
    //        latch.countDown();

    //        ExecutorService executorpool=Executors. newFixedThreadPool(4);
    //        CyclicBarrier cyclicBarrier= new CyclicBarrier(4);
    //
    //        CycWork work1= new CycWork(cyclicBarrier, "张三" );
    //        CycWork work2= new CycWork(cyclicBarrier, "李四" );
    //        CycWork work3= new CycWork(cyclicBarrier, "王五" );
    //        CycWork work4= new CycWork(cyclicBarrier, "王五1" );
    //
    //        executorpool.execute(work1);
    //        executorpool.execute(work2);
    //        executorpool.execute(work3);
    //        executorpool.execute(work4);
    //
    //        executorpool.shutdown();
    //
    //    }

    static class TaskThread extends Thread {

        CountDownLatch latch;

        public TaskThread(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {

            try {
                latch.await();
                System.out.println(getName() + " start " + System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static class CycWork implements Runnable {


        private CyclicBarrier cyclicBarrier;
        private String        name;

        public CycWork(CyclicBarrier cyclicBarrier, String name) {
            this.name = name;
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub

            System.out.println(name + "正在打桩，毕竟不轻松。。。。。");

            try {
                Thread.sleep(1000);
                System.out.println(name + "不容易，终于把桩打完了。。。。");
                cyclicBarrier.await();

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            System.out.println(name + "：其他逗b把桩都打完了，又得忙活了。。。");


        }

    }

    static class ReentrantLockTest extends Thread {

        ReentrantLock lock = new ReentrantLock(true);
        static int i = 0;

        public ReentrantLockTest(String name) {
            super.setName(name);
        }

        @Override
        public void run() {
            for (int j = 0; j < 100; j++) {
                lock.lock();
                try {
                    System.out.println(this.getName() + " " + i);
                    i++;
                } finally {
                    lock.unlock();
                }
            }
        }

        /**
         * @param args
         * @throws InterruptedException
         */
        public static void main(String[] args) throws InterruptedException {
            ReentrantLockTest test1 = new ReentrantLockTest("thread1");
            ReentrantLockTest test2 = new ReentrantLockTest("thread2");

            test1.start();
            test2.start();
            //            test1.join();
            //            test2.join();
            System.out.println(i);
        }
    }
}
