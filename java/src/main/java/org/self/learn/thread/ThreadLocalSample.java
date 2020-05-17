package org.self.learn.thread;

import java.lang.reflect.Field;

/**
 * 线程本地变量.
 * <p>
 * 属于每个线程的变量，因此操作ThreadLocal不存在并发问题
 * <p>
 * ThreadLocal结构:
 * 内部定义了一个ThreadLocalMap<ThreadLocal<?>, ?>, 一个简易版的HashMap
 * <p>
 * 由于是线程变量，所以线程Thread类里面维护了一个实例变量ThreadLocalMap
 *
 * ThreadLocal并不是用来存储数据的，数据是Thread.ThreadLocalMap<ThreadLocal<?>, ?>来维护的
 * ThreadLocal只是定义了ThreadLocalMap的结构及Map的操作
 *
 *                                                               ==> createMap(this(指threadLocal), XXX)
 * set过程的结构理解: ThreadLocal -> ThreadLocalMap -> Thread      |null
 * threadLocal.set(XXX) ==> currentThread.getThreadLocalMap() ==>
 *                                                              |else
 *                                                               ==> map.set(this(指threadLocal), XXX)
 * <p>
 * 使用之后必须threadLocal.remove()删除这个变量
 *
 * <p>
 * ThreadLocalMap采用的开放地址法来解决hash冲突区别于HashMap的链地址法
 *
 * 内部原理:
 *
 * 创建map时:
 * createMap(this(指threadLocal), XXX) 创建一个容量为16的数组Entry<ThreadLocal, Object>[] tab
 * 每个ThreadLocal实例创建完成就有一个threadLocalHashCode
 *
 * 1: threadLocal.set(XXX) 通过哈希threadLocalHashCode & (length-1) 确认插入位置
 * 如果hash冲突（该位置存在Entry），采用开放地址法-并往下顺序探测直到出现一个开放地址（数组位置空值）,
 * 进行tab[i] = new Entry(key, value)插入，然后检查是否需要rehash() :
 * 当size > (2/3 * tab.length) 进行rehash()，内部进行清理过期(key为空)的Entry，
 * 然后如果size > (1/2 * tab.length)时，进行resize()按2倍扩容
 *
 * 2: threadLocal.get() 通过当前线程获取ThreadLocalMap，然后ThreadLocalMap.getEntry(this)
 * 内部hash找到tab的位置，如果在当前位置的Entry的ThreadLocal一致直接返回，
 * 否则通过etEntryAfterMiss()从当前位置往下顺序探测，探测到满足Entry的ThreadLocal与当前一致时，返回结果，
 * 在这个过程中如果发现存在Entry.ThreadLocal存在为null是，将作为过期数据被清除并rehash
 * （重新计算后面的Entry的hash位置，位置不一致时，将会来填充刚刚清除位置，****为了保持同一hash的这些Entry连续***）
 *
 * 3: threadLocal.remove() 同样获取到当前线程的ThreadLocalMap， 然后通过threadLocal自身的hash计算出位置
 * 然后通过当前位置逐个往后探测，如果ThreadLocal一致，就清除当前的Entry的ThreadLocal作为过期数据, 紧接着就清理过期数据（并rehash）
 *
 * <p>
 * tips:
 *     清除过期数据时, 内部时必须rehash的，保证hash冲突数据的连续性，
 *     以上3个方法都会涉及清除过期数据
 *
 * 同时出现下列情况时会出现内存泄漏:
 * <p>
 * 1:如果ThreadLocal除了被ThreadLocalMap引用外，没有别的引用
 * 2:线程没有停止（线程里while(true) {create ThreadLocal()....}）
 * 3:ThreadLocal.set(XXX)后，没有进行任何的get/set/remove操作
 * <p>
 * 通常防止被回收，需要对外加一个强引用，可以绑定到static private field将状态绑定到Thread
 * 使用完成后手动remove
 *
 * 如果使用Thread里面map使用HashMap<ThreadLocal, Object>,entry强引用ThreadLocal
 * 上面如果满足1和2之后，除了手动remove（在这get/set肯定不能删除Entry），也会导致内存泄漏
 *
 * 相对来说，使用weakReference的key(ThreadLocal)更容易回收，Entry中出现(null : value)后，
 * 通过get/set/remove操作均可以清除
 *
 * @date 2020-05-09.
 * @see "https://yq.aliyun.com/articles/32557"
 */
public class ThreadLocalSample {
    public static void main(String[] args) throws InterruptedException {
        ThreadLocal<Integer> integerThreadLocal = new ThreadLocal<>();
        ThreadLocal<String> integerThreadLocal1 = new ThreadLocal<>();

        integerThreadLocal.set(12);
        integerThreadLocal1.set("abc");

        System.out.println(Thread.currentThread().getName() + "::" + integerThreadLocal.get());
        System.out.println(Thread.currentThread().getName() + "::" + integerThreadLocal1.get());

        new Thread(() -> {
            integerThreadLocal.set(123);
            integerThreadLocal1.set("abcd");
            integerThreadLocal1.remove();

            System.out.println(Thread.currentThread().getName() + "::" + integerThreadLocal.get());
            System.out.println(Thread.currentThread().getName() + "::" + integerThreadLocal1.get());
        }).start();

        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + "::" + integerThreadLocal1.get());

        integerThreadLocal.remove();
        integerThreadLocal1.remove();
    }

    //threadLocalHashCode & (len - 1)得到一个趋于均匀的slot槽位
    //引用：https://www.jianshu.com/p/56f64e3c1b6c
    static public class ThreadLocalTest {

        public static void main(String[] args) {
            printAllSlot(8);
            printAllSlot(16);
            printAllSlot(32);
        }

        static void printAllSlot(int len) {
            System.out.println("********** len = " + len + " ************");
            for (int i = 1; i <= 64; i++) {
                ThreadLocal<String> t = new ThreadLocal<>();
                int slot = getSlot(t, len);
                System.out.print(slot + " ");
                if (i % len == 0)
                    System.out.println(); // 分组换行
            }
        }

        /**
         * 获取槽位
         *
         * @param t   ThreadLocal
         * @param len 模拟map的table的length
         */
        static int getSlot(ThreadLocal<?> t, int len) {
            int hash = getHashCode(t);
            return hash & (len - 1);
        }

        /**
         * 反射获取 threadLocalHashCode 字段，因为其为private的
         */
        static int getHashCode(ThreadLocal<?> t) {
            Field field;
            try {
                field = t.getClass().getDeclaredField("threadLocalHashCode");
                field.setAccessible(true);
                return (int) field.get(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
