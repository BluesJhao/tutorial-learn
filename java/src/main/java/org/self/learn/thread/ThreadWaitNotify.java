package org.self.learn.thread;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 线程通信
 * <p>
 * 对象的wait()让当前对象监视器的owner线程暂停处于waiting状态
 * notify()唤醒等待当前对象监视器的(waitset或者entryList)一个线程，使其能够参与竞争owner
 * notifyAll()唤醒等待当前对象监视器的(waitset)所有线程，
 * 使所有(waitset)能够参与竞争owner，waitset里面的线程列表均会被执行, 但是只能一个个抢到锁后执行
 * <p>
 * tips:
 * 当前线程必须拥有对象的monitor
 *
 * @date 2020-05-03.
 */
public class ThreadWaitNotify {
    public static void main(String[] args) throws InterruptedException {
        TaskQueue q = new TaskQueue();
        ArrayList<Thread> ts = new ArrayList<Thread>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        String s = q.getTask();
//                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            };
            t.start();
            ts.add(t);
        }
        for (int i = 0; i < 2; i++) {
            Thread add = new Thread(() -> {
                // 放入task:
                String s = new Random().nextInt() + "";
                try {
                    q.addTask(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            add.start();
        }

        Thread.sleep(50000);
        for (Thread t : ts) {
            t.interrupt();
        }
    }

    static class TaskQueue {
        private final Object lock = new Object();
        Queue<String> queue = new LinkedList<>();

        void addTask(String s) throws InterruptedException {
            System.out.println(Thread.currentThread().getName() + "waiting");
            synchronized (lock) {
                System.out.println("acquire add task lock : " + s + "==" + Thread.currentThread().getName() + "==" +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));

                this.queue.add(s);
                System.out.println(
                        "already add task and notify: " + s + "==" + Thread.currentThread().getName() + "==" +
                                new SimpleDateFormat("HH:mm:ss").format(new Date()));
                lock.notifyAll();
                Thread.sleep(6000);
            }
        }

        String getTask() throws InterruptedException {
            synchronized (lock) {
                System.out.println("get task lock :" + Thread.currentThread().getName() + " ==" +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));

                while (queue.isEmpty()) {
                    System.out.println("get task waiting :" + Thread.currentThread().getName() + " ==" +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    lock.wait();
                    Thread.sleep(5000);
                    System.out.println("already wake up :" + Thread.currentThread().getName() + " ==" +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
                }

                String remove = queue.remove();

                System.out.println("get success : " + remove + "==" + Thread.currentThread().getName() + " ==" +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
                return remove;
            }
        }
    }
}

