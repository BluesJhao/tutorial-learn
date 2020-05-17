package org.self.learn.thread;

/**
 * volatile关键字, 修饰的是线程间的共享变量, 保证变量的可见性,
 * <p>
 * 但是保证不了安全性，就是非线程安全
 * <p>
 * tips: 线程的执行涉及【CPU 寄存器 缓存(L1，L2)】, 主存
 * <p>
 * 缓存一致性协议（MESI协议）它确保每个缓存中使用的共享变量的副本是一致的。
 * 其核心思想如下：当某个CPU在写数据时，如果发现操作的变量是共享变量，
 * 则会通知其他CPU告知该变量的缓存行是无效的，因此其他CPU
 * 在读取该变量时，发现其无效会重新从主存中加载数据。
 * <p>
 * volatile并不能保证线程安全。
 *
 * @date 2020-05-01.
 */
public class VolatileSample {
    static int i = 1;

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        Timer timer = new Timer();
        timer.startWork();
        for (int i = 0; i < 10; i++) {
            Thread thread1 = new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counter.increment();

            });
            thread1.start();

            Thread thread2 = new Thread(new Runn());
            thread2.start();

            Thread thread3 = new Thread(timer::work);
            thread3.start();
            //            Thread.sleep(1000);

            new Thread(timer::end).start();
        }
        Thread.sleep(10000);
        System.out.println(counter.getValue());

        //及时具有可见性，也非线程安全
        System.out.println("共享变量自增操作结果: " + i);

    }

    static class Runn implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(i++);
        }
    }

    static class Timer extends Thread {

        private volatile boolean isWorking = false;

        public void startWork() {
            this.isWorking = true;
        }

        public void end() {
            this.isWorking = false;
        }

        public void work() {
            if (this.isWorking) {
                System.out.println(Thread.currentThread().getName() + ": start to work...");
            } else {
                System.out.println(Thread.currentThread().getName() + ": already ending...");
            }
        }

    }

    /**
     * 计数器，读写锁
     */
    static class Counter {

        private volatile int value;

        public int getValue() {
            return value;
        }

        public synchronized int increment() {
            return value++;
        }
    }
}
