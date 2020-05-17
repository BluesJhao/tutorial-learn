package org.self.learn.interview;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * comment
 * <p>
 * 阻塞队列：https://www.infoq.cn/article/java-blocking-queue
 * <p>
 * ArrayBlockingQueue
 * LinkedBlockingQueue
 * SynchronousQueue
 * <p>
 * 方法\处理方式	抛出异常	    返回特殊值	一直阻塞	    超时退出
 * 插入方法	    add(e)	    offer(e)	put(e)	    offer(e,time,unit)
 * 移除方法	    remove()	poll()	    take()	    poll(time,unit)
 * 检查方法	    element()	peek()	    不可用	    不可用
 * <p>
 * offer(e)返回true||false
 * poll()无返回值
 *
 * @date 2019-11-07.
 */
public class BlockingQueueTest {

    static LinkedBlockingQueue queue = new LinkedBlockingQueue();

    static class Producer implements Runnable {

        LinkedBlockingQueue queue;

        public Producer(LinkedBlockingQueue queue) {
            this.queue = queue;
        }


        @Override
        public void run() {

            for (int i = 0; i < 5; i++) {
                String e = Thread.currentThread().getName() + "==" + i;
                System.out.println("生产消息: " + (queue.offer(e) ? e : null));
            }
        }
    }

    static class Consumer implements Runnable {

        LinkedBlockingQueue queue;

        public Consumer(LinkedBlockingQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {

            for (int i = 0; i < 5; i++) {
                try {
                    System.out.println("消费消息: " + queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new Thread(new Producer(queue)).start();
            new Thread(new Consumer(queue)).start();
            new Thread(new Consumer(queue)).start();
        }
    }
}
