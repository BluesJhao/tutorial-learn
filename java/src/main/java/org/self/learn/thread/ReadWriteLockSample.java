package org.self.learn.thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁.依赖AQS实现
 * <p>
 * 解决多线程同时读，但只有一个线程能写的问题
 * <p>
 * readLock: 读共享锁, 在获取到读锁的时候，可以多个线程读，只要没有线程写
 * writeLock: 写独占锁, 同一时刻只能由一个线程写
 * <p>
 * (单个或多个)读的的时候不能写，写的时候不能读
 *
 * @date 2020-05-05.
 */
public class ReadWriteLockSample {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Lock          writeLock     = readWriteLock.writeLock();
        Lock          readLock      = readWriteLock.readLock();

        int anInt = 0;

        void write() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            System.out.println("try acquire write lock..." + threadName + "==" +
                    new SimpleDateFormat("HH:mm:ss").format(new Date()));
            writeLock.lock();
            System.out.println("already acquire write lock..." + threadName + "==" +
                    new SimpleDateFormat("HH:mm:ss").format(new Date()));
            anInt++;
            System.out.println(
                    "==write==" + anInt + "==" + threadName + "==" +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
            Thread.sleep(20000);
            writeLock.unlock();
        }

        int read() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            System.out.println("try acquire read lock..." + threadName + "==" +
                    new SimpleDateFormat("HH:mm:ss").format(new Date()));
            readLock.lock();
            System.out.println("already acquire read lock..." + threadName + "==" +
                    new SimpleDateFormat("HH:mm:ss").format(new Date()));
            Thread.sleep(2000);
            System.out.println(
                    "==read==" + anInt + "==" + threadName + "==" +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
            readLock.unlock();
            return anInt;
        }
    }
}
