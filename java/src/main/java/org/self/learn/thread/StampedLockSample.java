package org.self.learn.thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.StampedLock;

/**
 * 基于时间戳的读写锁.
 * <p>
 * 在ReadWriteLock的基础上提供了更高并发性的方案
 * <p>
 * 提供了: writeLock()让同一时刻只有一个线程写，
 * 此时tryOptimisticRead(), 多线程可以通过乐观锁来乐观读取，
 * 通过判断乐观锁的时间戳是否有效(获取乐观锁时间戳后，是否有通过独占的方式获取锁，此外时间戳为0时，无效)，
 * 判断数据被修改过，如果被修改过，此时获取readLock()读锁（采用了ReadWriteLock的模式），重新获取最新数据。
 * <p>
 * 内部同样维护了WNode队列，但并没有采用AQS
 *
 * @date 2020-05-05.
 * @see StampedLock 从1.8开始提供
 */
public class StampedLockSample {

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        for (int i = 0; i < 10; i++) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        counter.write();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        counter.read();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

        Thread.sleep(90000);

        System.out.println("total: " + counter.read());
    }

    static class Counter {
        StampedLock stampedLock = new StampedLock();

        int anInt = 0;

        void write() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            System.out.println("try acquire write lock..." + threadName + "==" +
                    new SimpleDateFormat("HH:mm:ss").format(new Date()));
            long writeStampLock = stampedLock.writeLock();

            System.out.println("already owner write lock..." + threadName + "==" +
                    new SimpleDateFormat("HH:mm:ss").format(new Date()));
            anInt++;
            System.out.println(
                    ">>write==" + anInt + "==" + threadName + "==" +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
            stampedLock.unlockWrite(writeStampLock);
        }

        int read() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            System.out.println("try acquire optimistic lock..." + threadName + "==" +
                    new SimpleDateFormat("HH:mm:ss").format(new Date()));
            long optimisticReadStampLock = stampedLock.tryOptimisticRead();
            System.out.println("already owner optimistic lock..." + threadName + "==" +
                    new SimpleDateFormat("HH:mm:ss").format(new Date()));
            //            Thread.sleep(6000);
            System.out.println("<<optimistic read==" + anInt + "==" + threadName + "==" +
                    new SimpleDateFormat("HH:mm:ss").format(new Date()));
            //            Thread.sleep(100);// mark anInt maybe has been modified
            if (!stampedLock.validate(optimisticReadStampLock)) {
                //当optimisticReadStampLock值为0时，代表有线程获取了writeLock(可能对数据进行了修改)
                //此时需要获取readLock来将获取writeLock的线程阻塞，此时读取或者操作相关数据，相当于恢复到ReadWriteLock模式
                //如果writeLock还没释放，stampedLock.readLock()将等待
                long readLock = stampedLock.readLock();
                System.out.println(//anInt is newest
                        "<<Non-exclusively read==" + anInt + "==" + threadName + "==" +
                                new SimpleDateFormat("HH:mm:ss").format(new Date()));
                Thread.sleep(10000);// mark writeLock blocking
                stampedLock.unlockRead(readLock);
                return anInt;
            }
            System.out.println(
                    "<<read==" + anInt + "==" + threadName + "==" +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
            return anInt;
        }
    }
}
