package org.self.learn.thread;

import java.util.concurrent.*;

/**
 * 创建线程的多种方式
 * <p>
 * 1: 继承Thread，并实例化new XXXThread()
 * 2: 实现Runnable接口，将Runnable实例作为Thread的构造参数 new Thread(new Runnable(){})
 * 3: 实现Callable接口，通过实例化FutureTask来创建线程 new Thread(new FutureTask(new Callable(){}))
 * 4: 通过线程池创建线程 new ThreadPoolExecutor(...), 通过submit(Runnable)
 *
 * @date 2020-05-01.
 */
public class ThreadCreator {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Thread tempThread = new TempThread();
        tempThread.start();

        Runnable runnable = () -> System.out.println(Thread.currentThread().getName());

        Thread thread = new Thread(runnable);
        thread.start();

        //实现callable接口的call方法,有返回值,
        //FeatureTask是实现了Runnable，创建的线程有返回结果
        //使用FutureTask.get(),当前线程将阻塞直到获取到结果
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() {
                return 2;
            }
        };
        FutureTask<Integer> target = new FutureTask<>(callable);
        Thread thread1 = new Thread(target);
        thread1.start();

        //        Thread.interrupt();
        //        target.cancel(true);

        System.out.println(target.get());

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.submit(runnable);
    }

    private static class TempThread extends Thread {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName());
        }
    }

}
