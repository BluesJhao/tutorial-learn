package org.self.learn.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CyclicBarrier: 与CountDownLatch相似，也是通过计数器实现线程同步
 * <p>
 * 多个线程（或一个）在各自线程内部都完成一部分操作后(计数减1)，总的计数器归0后
 * 然后可以各自进行后续的操作（像水闸打开后，这些在此等待的线程将可以执行后续各自的操作）
 * 但在计数器为归0时，这些线程(CyclicBarrier.await())都将处于等待状态
 * <p>
 * 与CountDownLatch的区别:
 * <p>
 * 都是计数器为0时，线程被唤醒；
 * <p>
 * CountDownLatch是一个线程（多个）等（await）其他线程（coundown()）,属于汇报
 * CyclicBarrier是各自线程之间相互等待，属于协同
 * <p>
 * CountDownLatch关闭后不能再用，CyclicBarrier可以重用
 *
 * @date 2020-05-06.
 */
public class CyclicBarrierSample {
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        //计数量
        int parties = 5;

        //创建计数器
        CyclicBarrier barrier = new CyclicBarrier(parties);

        //创建线程池，可以通过以下方式创建
        ExecutorService threadPool = Executors.newFixedThreadPool(parties);

        System.out.println("公司发送通知，每一位员工在周六早上8点【自驾车】到公司大门口集合");
        for (int i = 1; i < parties; i++) {
            //将子线程添加进线程池执行
            threadPool.execute(new Employee(barrier, i));
        }
        try {
            Thread.sleep(5000);
            barrier.await();//计数器为0后，会重置计数器parties
            System.out.println("所有人准备【自驾车】前往目的地");
            //阻塞当前线程，直到所有员工到达公司大门口之后才执行
            // 使当前线程在锁存器倒计数至零之前一直等待，除非线程被中断或超出了指定的等待时间。
            //latch.await(long timeout, TimeUnit unit)
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }

        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    cyclicBarrier.await();
                } catch (Exception ignored) {
                }
            }
        });
        thread.start();
        thread.interrupt();
        try {
            cyclicBarrier.await();
        } catch (Exception e) {
            System.out.println("thread interrupted = " + cyclicBarrier.isBroken());
            //***计数器可以中断后重置***
            cyclicBarrier.reset();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        cyclicBarrier.await();
                    } catch (Exception ignored) {
                    }
                }
            }).start();
            cyclicBarrier.await();
            System.out.println("execute once again");
        }
    }
}

//分布式工作线程
class Employee implements Runnable {

    private CyclicBarrier barrier;
    private int           employeeIndex;

    public Employee(CyclicBarrier barrier, int employeeIndex) {
        this.barrier = barrier;
        this.employeeIndex = employeeIndex;
    }

    @Override
    public void run() {
        try {
            System.out.println("员工：" + employeeIndex + "，正在前往公司大门口集合...");
            Thread.sleep(1000 * employeeIndex);
            System.out.println("员工：" + employeeIndex + "，已到达。");
            barrier.await();//当前线程waiting直到计数到达parties；而CountDownLatch.countDown()之后，后面继续执行
            Thread.sleep(1000);
            System.out.println("员工：" + employeeIndex + "，【自驾车】前往目的地");
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}