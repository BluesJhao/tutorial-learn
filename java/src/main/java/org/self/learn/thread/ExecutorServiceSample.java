package org.self.learn.thread;

import java.util.List;
import java.util.concurrent.*;

/**
 * 线程池.
 * <p>
 * 统一创建和管理线程，队列存Runnable任务
 * <p>
 * 简单方便的同时又带来弊端:
 * FixThreadPool可能会带来大量任务堆积到队列，
 * CachedThreadPool可能会不断创建线程直至撑爆内存
 *
 * @date 2020-05-08.
 */
public class ExecutorServiceSample {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        //创建固定大小线程数(核心线程数&最大线程数)的线程池，线程固定，使用无界阻塞队列（LinkedBlockingQueue默认Integer.MAX_VALUE）
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        //创建Cached类型的线程池（无参）, coreSize=0, maxSize=Integer.MAX_VALUE,
        //使用SynchronousQueue队列，重点是超过core之外的核心线程数，将会有60秒的缓存时间
        ExecutorService executorService1 = Executors.newCachedThreadPool();
        //创建一个调度类型的线程池，可以按照时间间隔来进行调度
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

        //实际开发中都是定制ThreadPool的相关参数
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1));
        System.out.println("executor starting>>>>>" + threadPoolExecutor);
        //1:查看提交任务时，看线程池的变化
        for (int i = 0; i < 3; i++) {
            /*提交任务有两种:
             1: 通过submit(Runnable的实例)，内部统一转换为FutureTask（也实现了Runnable），
             可以通过FutureTask获取执行是否完成及执行结果
             2: 通过execute(Runnable的实例), 没有执行结果
             */
            Future<?> submitFuture = threadPoolExecutor.submit(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + ":hello world");
                return "TaskFuture result";//可以有返回值
            });
            //当前线程将会阻塞，直到获取结果（内部进行park与unpark）
            //System.out.println("Future result is: " + submitFuture.get());
            System.out.println(threadPoolExecutor);
        }
        Thread.sleep(10000);
        System.out.println("executor ending>>>>>" + threadPoolExecutor);

        System.out.println("======================================");

        //使用SynchronizedQueue这个阻塞队列的效果
        //SynchronizedQueue是一个没有容量概念的（同步队列）阻塞队列，
        //放入的runnable，必须等到有线程来消费之后才能重新放入，可以看做只能存一个元素
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 2, 1, TimeUnit.SECONDS,
                new SynchronousQueue<>(false));

        System.out.println("SynchronousQueue executor starting>>>>>" + threadPoolExecutor);
        for (int i = 0; i < 5; i++) {
            poolExecutor.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + ":SynchronousQueue hello world");
            });
            //当前线程将会阻塞，直到获取结果（内部进行park与unpark）
            //System.out.println("Future result is: " + submitFuture.get());
            System.out.println("SynchronousQueue:" + poolExecutor);
        }
        Thread.sleep(10000);
        System.out.println("SynchronousQueue executor ending>>>>>" + poolExecutor);

        /*
        boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
        Runnable r = timed ?
                workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                workQueue.take();*/
        //2: 线程池中的worker在空闲的时候的存活情况取决allowCoreThreadTimeOut和corePoolSize(***备注：空闲的线程被阻塞队列阻塞***)
        //正常情况下（没有设置allowCoreThreadTimeOut），当线程数超过corePoolSize时，空闲时间超过keepAliveTime时当前线程（worker）将会被删除
        //如果threadPoolExecutor.allowCoreThreadTimeOut(true)时，keepAliveTime必须设置大于零，核心线程在空闲时间超过keepAliveTime时也都将会被删除
        //例如如下线程池: threadPoolExecutor1里面的线程数量为0
        ThreadPoolExecutor threadPoolExecutor1 = new ThreadPoolExecutor(2, 3, 1, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1));
        //所有线程(主要是核心线程数)空闲时是否删除，超出核心线程数的线程不管怎样（过了keepAliveTime有效期）都会被删除
        threadPoolExecutor1.allowCoreThreadTimeOut(true);
        for (int i = 0; i < 3; i++) {
            threadPoolExecutor1.submit(() -> System.out.println("All worker(thread) well be removed"));
        }
        Thread.sleep(3000);
        System.out.println("threadPoolExecutor1 result>>>>>" + threadPoolExecutor1);

        System.out.println("======================================");
        // 线程池终止方式:
        // 立即终止->将正在执行的线程interrupt，queue中的任务返回，将不再接收新的任务。
        // 终止   ->正常执行任务，直到将queue任务消费完毕, 将不再接收新的任务。
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            threadPoolExecutor1.submit(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }//repeat times verify termination or not
                System.out.println("Runnable " + finalI + " was executed ?");
                System.out.println("Runnable " + finalI + " was executed ?");
                System.out.println("Runnable " + finalI + " was executed ?");
            });
        }
        //threadPoolExecutor1.shutdown(); state -> shutdown
        List<Runnable> runnables = threadPoolExecutor1.shutdownNow();//state -> stop
        System.out.println(runnables);
        try {
            threadPoolExecutor1.submit(() -> System.out.println("yet add ?"));
        } catch (Exception ex) {
            System.out.println("can't add runnable");
        }
        Thread.sleep(10);//for waiting interrupt
        System.out.println("pool was shutdown ? : " + threadPoolExecutor1.isShutdown());
        //terminated is true if use shutdownNow
        System.out.println("pool was terminated ? : " + threadPoolExecutor1.isTerminated());
        //terminated is true if use shutdown
        System.out.println("pool was terminating ? : " + threadPoolExecutor1.isTerminating());

        Thread.sleep(5000);

        System.out.println("======================================");

        //3: 提交的任务超出队列的长度+最大线程数时，看默认的任务的丢弃策略
        //有四个丢弃策略: 抛出异常并丢弃（默认），丢弃当前任务，丢弃队列头任务，在主线程中执行
        for (int i = 0; i < 4; i++) {
            threadPoolExecutor.submit(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("hello world");
            });
            System.out.println(threadPoolExecutor);
        }
        //主线程抛出异常，所以这里执行不到
        System.out.println("executor result:" + threadPoolExecutor);
    }

    //模拟ThreadPoolExecutor中的Work
    static class Worker implements Runnable {
        //初始化1
        Runnable runnable;
        Thread   thread;

        {
            //初始化2
            System.out.println(runnable);
        }

        Worker(Runnable runnable) {
            //初始化3
            this.runnable = runnable;
            this.thread = new Thread(this);
            this.run();
        }

        @Override
        public void run() {
            runnable.run();
        }

        public static void main(String[] args) throws InterruptedException {
            Worker worker = new Worker(() -> System.out.println("hello"));
            worker.thread.start();
            Thread.sleep(1000);
            System.out.println(worker.thread.getState());
            Thread.sleep(10000);
        }
    }
}
