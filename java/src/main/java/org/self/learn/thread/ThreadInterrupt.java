package org.self.learn.thread;

/**
 * 调用线程的interrupt方法只是设置一个中断信号，并非会立马会中断，依赖操作系统
 *
 * @date 2020-05-01.
 */
public class ThreadInterrupt {

    public static void main(String[] args) throws InterruptedException {
//        Thread t = new Thread(() -> {
//            int n = 0;
//            while (!Thread.interrupted()) {
//                n++;
//                System.out.println(n + " hello!");
//            }
//        });
        Thread t = new MyThread();
        t.start();
        Thread.sleep(1); // 暂停1毫秒
        t.interrupt(); // 中断t线程
        t.join(); // 等待t线程结束
        System.out.println("end");
    }

    static class MyThread extends Thread {
        public void run() {
            Thread hello = new HelloThread();
            hello.start(); // 启动hello线程
            try {
                hello.join(); // 等待hello线程结束
            } catch (InterruptedException e) {
                System.out.println("MyThread interrupted!");
            }
            hello.interrupt();
        }
    }

    static class HelloThread extends Thread {
        public void run() {
            int n = 0;
            while (!isInterrupted()) {
                n++;
                System.out.println(n + " hello!");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("HelloThread interrupted");
                    break;
                }
            }
        }
    }

}
