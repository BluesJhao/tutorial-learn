package org.self.learn.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;

/**
 * 原子操作工具类.
 * <p>
 * 通CAS自旋实现，并发修改
 * <p>
 * CAS会导致:
 * ABA问题, 加版本可以解决, AtomicStampedReference可以解决
 * 循环时间长开销大
 *
 * @date 2020-05-08.
 */
public class AtomicSample {
    public static void main(String[] args) throws InterruptedException {
        AtomicLong atomicLong = new AtomicLong();
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(atomicLong.incrementAndGet());
                }
            }).start();
        }

        Thread.sleep(1000);

        int[] ints = {1, 2, 3, 4, 5, 6};
        //支持并行数组操作
        AtomicIntegerArray integerArray = new AtomicIntegerArray(ints);

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.printf("%20s : %s\n", "getAndDecrement(0)", integerArray.getAndDecrement(0));
                    System.out.printf("%20s : %s\n", "decrementAndGet(1)", integerArray.decrementAndGet(1));
                    System.out.printf("%20s : %s\n", "getAndIncrement(2)", integerArray.getAndIncrement(2));
                    System.out.printf("%20s : %s\n", "incrementAndGet(3)", integerArray.incrementAndGet(3));
                    System.out.printf("%20s : %s\n", "addAndGet(100)", integerArray.addAndGet(0, 100));
                    System.out.printf("%20s : %s\n", "getAndAdd(100)", integerArray.getAndAdd(1, 100));
                    System.out.printf("%20s : %s\n", "compareAndSet()", integerArray.compareAndSet(5, 6, 1000));
                    System.out.printf("%20s : %s\n", "get(2)", integerArray.get(2));
                }
            }).start();
        }

        Thread.sleep(1000);

        //ABA问题重现
        AtomicInteger index = new AtomicInteger(10);
        new Thread(() -> {
            index.compareAndSet(10, 11);
            index.compareAndSet(11, 10);
            System.out.println(Thread.currentThread().getName() +
                    "：10->11->10");
        }, "张三").start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                boolean isSuccess = index.compareAndSet(10, 12);
                System.out.println(Thread.currentThread().getName() +
                        "：过了一段时间, index是预期的还是10嘛，" + isSuccess
                        + "   设置的新值是：" + index.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "李四").start();

        Thread.sleep(3000);

        //解决ABA问题
        AtomicStampedReference<Integer> stampRef
                = new AtomicStampedReference<>(10, 1);
        new Thread(() -> {
            int stamp = stampRef.getStamp();
            System.out.println(Thread.currentThread().getName()
                    + " 第1次版本号： " + stamp);
            stampRef.compareAndSet(10, 11, stampRef.getStamp(), stampRef.getStamp() * 10 + 1);
            System.out.println(Thread.currentThread().getName()
                    + " 第2次版本号： " + stampRef.getStamp());
            stampRef.compareAndSet(11, 10, stampRef.getStamp(), stampRef.getStamp() * 10 + 1);
            System.out.println(Thread.currentThread().getName()
                    + " 第3次版本号： " + stampRef.getStamp());
        }, "张三").start();

        new Thread(() -> {
            try {
                int stamp = stampRef.getStamp();
                System.out.println(Thread.currentThread().getName()
                        + " 第1次版本号： " + stamp);
                TimeUnit.SECONDS.sleep(2);
                boolean isSuccess = stampRef.compareAndSet(10, 12,
                        stampRef.getStamp(), stampRef.getStamp() * 10 + 1);
                System.out.println(Thread.currentThread().getName()
                        + " 修改是否成功： " + isSuccess + " 当前版本 ：" + stampRef.getStamp());
                System.out.println(Thread.currentThread().getName()
                        + " 当前实际值： " + stampRef.getReference());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "李四").start();

        Thread.sleep(3000);

        // 创建两个Person对象，它们的id分别是101和102。
        Person p1 = new Person(101);
        Person p2 = new Person(102);
        // 新建AtomicReference对象，初始化它的值为p1对象
        AtomicReference ar = new AtomicReference(p1);
        // 通过CAS设置ar。如果ar的值为p1的话，则将其设置为p2。
        ar.compareAndSet(p1, p2);

        Person p3 = (Person) ar.get();
        System.out.println("p3 is " + p3);
        System.out.println("p3.equals(p1)=" + p3.equals(p1));

    }

    static class Person {
        volatile long id;

        public Person(long id) {
            this.id = id;
        }

        public String toString() {
            return "id:" + id;
        }
    }
}
