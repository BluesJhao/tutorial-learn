package org.self.learn.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * 并行集合操作.
 *
 * @date 2020-05-06.
 */
public class ConCurrencyCollection {

    public static void main(String[] args) {
        CopyOnWriteArrayListSample[] arrayList = new CopyOnWriteArrayListSample[]{
                new CopyOnWriteArrayListSample(), new CopyOnWriteArrayListSample()};
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            arrayList.clone();//Arrays.copyOf(arrayList, 2);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);

        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            //底层Native System.arraycopy()
            Arrays.copyOf(arrayList, arrayList.length);
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    static class ConcurrentHashMapSample {
        public static void main(String[] args) {
            //非线程安全操作
            HashMap<String, Integer> hashMap = new HashMap<>();
            ConcurrentHashMap<String, Integer> concurrenttHashMap = new ConcurrentHashMap<>();
            for (int i = 0; i < 10; i++) {
                int finalI = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        concurrenttHashMap.put("key" + finalI, finalI);
                    }
                }).start();
                concurrenttHashMap.forEach((s, integer) -> {
                    System.out.println(s + "=" + integer);
                });
            }
        }
    }

    static class CopyOnWriteArrayListSample {
        public static void main(String[] args) throws InterruptedException {
            //非线程安全操作
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                int finalI = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        arrayList.add(finalI * 2);
                    }
                }).start();
                //会抛出ConcurrentModificationException
                System.out.println(arrayList);
            }

            //线程安全的ArrayList，
            // 如其名 在进行写(同步操作)相关操作时都是通过底层System.arraycopy出新的数组副本，内部没有采用一个固定的数组+size
            CopyOnWriteArrayList<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
            for (int i = 0; i < 20; i++) {
                int finalI = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        copyOnWriteArrayList.add(finalI);
                    }
                }).start();
                System.out.println(copyOnWriteArrayList);
            }
        }
    }

    static class CopyOnWriteArraySetSample {
        public static void main(String[] args) {

            //线程安全Set，基于CopyOnWriteArrayList实现，在判断可重复时，用插入的数据与Array中的对象进行equals
            CopyOnWriteArraySet<Integer> objects = new CopyOnWriteArraySet<>();
            for (int i = 0; i < 20; i++) {
                int finalI = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        objects.add(finalI);
                    }
                }).start();
                System.out.println(objects);
            }
        }
    }

    static class ConcurrentSkipListSetSample {
        public static void main(String[] args) {
            ConcurrentSkipListMap<Object, Object> skipListMap = new ConcurrentSkipListMap<>();
            for (int i = 0; i < 100; i++) {
                int finalI = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        skipListMap.put("key" + finalI, finalI);
                    }
                }).start();
            }
            System.out.println(skipListMap);
        }
    }

    static class ConcurrentSkipListMapSample {
        public static void main(String[] args) {
            ConcurrentSkipListSet<Object> skipListMap = new ConcurrentSkipListSet<>();
            for (int i = 0; i < 100; i++) {
                int finalI = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        skipListMap.add("key" + finalI);
                    }
                }).start();
            }
            System.out.println(skipListMap);
        }
    }
}
