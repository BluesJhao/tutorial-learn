package org.self.learn.thread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * AQS:度列同步器: 维护了一个线程等待队列:可以通过它实现非公平公平锁，读写锁等...
 * <p>
 * 参考:https://juejin.im/post/5d34502cf265da1baf7d27aa
 * <p>
 * ReentrantLock(可重入锁)是通过AQS(AbstractQueuedSerializer)实现的公平和非公平锁
 * <p>
 * ReentrantLock相比synchronized关键字及通过对象监视器的wait及notify来说拥有更好的灵活性
 * <p>
 * 1:支持手动获取锁及释放锁（lock及unlock）
 * 2:支持尝试获取锁（tryLock）如果没有获取到可以做别的操作
 * 3:支持中断等待和非中断等待(awaitUninterruptibly())
 * <p>
 * 默认ReentrantLock是非公平锁, 公平锁与非公平锁的区别（判断hasQueuedPredecessors()）
 * <p>
 * 非公平锁在获取锁时，直接通过CAS去争抢锁，如没有获取到则会进入tryAcquire(), 如果获取不到，进入线程等待队列
 * 公平锁获取锁时，tryAcquire()中先判断队列是否有线程等待，如有->[非当前线程，入队； 当前线程，重入], 否则进行CAS抢锁
 * <p>
 * 非公平锁，具有较高的性能；公平锁相当于顺序请求，具有公平性
 * <p>
 * tips:
 * <p>
 * acquireQueued()入队时, 对当前线程进行LockSupport.park(currentThread)暂停操作
 * tryRelease(arg)时，对等待的线程队列进行LockSupport.unpark(队列头Node的thread)唤醒头结点线程
 * 达到了顺序唤醒的目的，巧妙的替换了通过对象监视器（无法对具体的线程进行暂停与唤醒）进行wait/notify
 * 等待或者唤醒相对对象监视器的通信来说，（直接将队列中的线程park或者unpark）将会占用更少的CPU资源
 * <p>
 * ReentrantLock的通信通过一个或多个Condition来实现
 * <p>
 * 实则通过AQS的ConditionObject的firstWaiter队列与lastWaiter来实现
 * <p>
 * （当前）condition上调用await时, firstWaiter队列维护condition.await()等待的线程，
 * 当condition.signal()或condition.signalAll()时，将firstWaiter进行入队并等待唤醒
 *
 * @date 2020-05-04.
 */
public class ReentrantLockSample {
    public static void main(String[] args) throws InterruptedException {

        Factory factory = new Factory();

        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                String productName = "product0::" + finalI;
                factory.produce(productName);
                System.out.println("Lock produce::" + productName);
            });
//            threads.add(thread);

            thread.start();
//            thread.interrupt();

            new Thread(() -> {
                String consume = null;
                try {
                    consume = factory.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Lock consume0::" + consume);
            }).start();
        }


        Thread.sleep(10000);

    }

    static class Factory {

        Lock          lock      = new ReentrantLock();
        Condition     condition = lock.newCondition();
        //        Condition     otherCondition = lock.newCondition();
        Queue<String> queue     = new LinkedList<>();

        void produce(String productName) {
            try {
                lock.lockInterruptibly();
            } catch (Exception e) {
                System.out.println("Interrupted... do something");
                return;
            }
            try {
                queue.add(productName);
                condition.signal();
            } finally {
                lock.unlock();
            }
        }

        String consume() throws InterruptedException {
//            lock.lock();
            try {
                while (queue.isEmpty()) {
                    condition.await();
                }
            } finally {
//                lock.unlock();
            }
            return queue.remove();
        }
    }
}
