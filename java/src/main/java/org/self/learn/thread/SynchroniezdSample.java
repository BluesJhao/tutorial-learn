package org.self.learn.thread;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Synchronized关键字只能修饰方法与方法内的代码块，修饰在static方法时成为类锁，最终锁的都是对象
 * <p>
 * 结论: 不管是放在方法上还是代码块所住得都是对象，如果方法有static修饰，表明修饰的是类的唯一的Class对象
 * <p>
 * Synchronized同步的底层实现:
 * <p>
 * 通过字节码反编译后方法上的synchronized会生成一个ACC_SYNCHRONIZED标志
 * 代码块的synchronized的开始进入monitorenter指令加锁，退出时通过monitorexit指令释放锁
 * <p>
 * 引自: https://mp.weixin.qq.com/s?__biz=MzI3NzE0NjcwMg==&mid=2650120537&idx=1&sn=f56201217c0ca6fde45ee12965b56296&chksm
 * =f36bbc78c41c356ee363367addcdc0b311afb2f9df86a7ee20d21348b3332fd64f273d6028ca&scene=21#wechat_redirect
 * <p>
 * 每个对象都有一个对象的监视器monitor，这个monitor对象里面记录着：
 * _owner: 此对象的监视器被哪个线程的拥有
 * _WaitSet: 等待线程队列（waiting状态）
 * _EntryList: 存放处于阻塞状态的线程队列（线程进入同步争抢时的多个线程）
 * _recursions: 重入锁次数，至0是释放
 * <p>
 * 引自: https://blog.csdn.net/w372426096/article/details/80079446
 *
 * @date 2020-05-02.
 */
public class SynchroniezdSample {

    public static void main(String[] args) {
        SyncThread syncThread = new SynchroniezdSample().new SyncThread();
        Thread B_thread1 = new Thread(syncThread, "B_thread1");
        Thread B_thread2 = new Thread(syncThread, "B_thread2");
        Thread C_thread1 = new Thread(syncThread, "C_thread1");
        Thread C_thread2 = new Thread(syncThread, "C_thread2");
        B_thread1.start();
        B_thread2.start();
        C_thread1.start();
        C_thread2.start();
    }

    class SyncThread implements Runnable {
        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            if (threadName.startsWith("B")) {
                sync1();
            } else if (threadName.startsWith("C")) {
                sync2();
            }
        }

        /**
         * 方法中有 synchronized(this|object) {} 同步代码块
         */
        void sync1() {
            System.out.println(Thread.currentThread().getName() + "_Sync1: " +
                    new SimpleDateFormat("HH:mm:ss").format(new Date()));
            synchronized (this) {
                try {
                    System.out.println(Thread.currentThread().getName() + "_Sync1_Start: " +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    Thread.sleep(5000);
                    System.out.println(Thread.currentThread().getName() + "_Sync1_End: " +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * synchronized 修饰非静态方法
         */
        synchronized void sync2() {
            System.out.println(Thread.currentThread().getName() + "_Sync2: " +
                    new SimpleDateFormat("HH:mm:ss").format(new Date()));
            try {
                System.out.println(Thread.currentThread().getName() + "_Sync2_Start: " +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName() + "_Sync2_End: " +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //        /**
        //         * 方法中有 synchronized(SyncThread.class)同步代码块
        //         */
        //        private void staticSync1() {
        //            System.out.println(Thread.currentThread().getName() + "_Sync1: " + new SimpleDateFormat
        //            ("HH:mm:ss").format(new Date()));
        //            synchronized (SyncThread.class) {
        //                try {
        //                    System.out.println(Thread.currentThread().getName() + "_Sync1_Start: " + new
        //                    SimpleDateFormat("HH:mm:ss").format(new Date()));
        //                    Thread.sleep(5000);
        //                    System.out.println(Thread.currentThread().getName() + "_Sync1_End: " + new
        //                    SimpleDateFormat("HH:mm:ss").format(new Date()));
        //                } catch (InterruptedException e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        }
        //        /**
        //         * synchronized 修饰静态方法
        //         */
        //        private static synchronized void staticSync2() {
        //            System.out.println(Thread.currentThread().getName() + "_Sync2: " + new SimpleDateFormat
        //            ("HH:mm:ss").format(new Date()));
        //            try {
        //                System.out.println(Thread.currentThread().getName() + "_Sync2_Start: " + new
        //                SimpleDateFormat("HH:mm:ss").format(new Date()));
        //                Thread.sleep(5000);
        //                System.out.println(Thread.currentThread().getName() + "_Sync2_End: " + new SimpleDateFormat
        //                ("HH:mm:ss").format(new Date()));
        //            } catch (InterruptedException e) {
        //                e.printStackTrace();
        //            }
        //        }
    }
}
