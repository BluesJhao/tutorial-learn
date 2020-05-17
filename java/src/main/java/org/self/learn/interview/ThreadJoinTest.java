package org.self.learn.interview;

/**
 * 保证多线程的顺序执行.
 * <p>
 * 当线程调用JOIN, 将等待这个线程执行完毕。
 * 当前线程A使用join后, 将后面可运行的线程B追加在其后面执行, 等待A执行完后才能执行；
 * 如果A join加入超时时间，超时后，后面的B或C线程将不再等待
 *
 * @date 2020-04-30.
 */
public class ThreadJoinTest {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("123");
            }
        });
        thread.start();
        thread.join();
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("234");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
        thread1.join(1000);
        System.out.println("main thread!");
    }
}
