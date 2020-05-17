package org.self.learn.interview;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 生产者消费者模式.
 *
 * @date 2020-05-04.
 */
public class ProducerConsumerMode {
    public static void main(String[] args) throws InterruptedException {

        Factory factory = new Factory();

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(() -> {
                String productName = "product::" + finalI;
                System.out.println("produce::" + productName);
                factory.produce(productName);
            }).start();

            new Thread(() -> {
                String consume = null;
                try {
//                    Thread.sleep(100);
                    consume = factory.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("consume::" + consume);
            }).start();
        }

        Thread.sleep(10000);

    }

    static class Factory {

        Queue<String> queue = new LinkedList<>();

        synchronized void produce(String productName) {
            queue.add(productName);
            this.notify();
        }

        synchronized String consume() throws InterruptedException {
            while (queue.isEmpty()) {
                this.wait();
            }
            return queue.remove();
        }
    }
}
