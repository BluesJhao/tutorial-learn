package org.self.learn.interview;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * Java引用类型的意义及使用场景.
 * <p>
 * Reference的产生主要是让JVM垃圾回收器对对象回收进行分类处理【需要回收的/什么时候回收/回收后的后续处理】
 * <p>
 * JAVA中的四种引用:
 * 1:强引用: A a = new A();
 * Gc root根据强可达来判断对象是否回收
 * <p>
 * 2:软引用: SoftReference<T> 实例对T实例的引用
 * Gc root根据软可达来判断对象是否回收，在oom之前会进行回收，适合一些内存敏感的缓存处理
 * 通过这两个字段判断内存不足时??? 堆可用大小，该SoftReference上一次调用get方法的时间都有关系
 * static private long clock;
 * private long timestamp;
 * <p>
 * 3:弱引用: WeakReference<T> 实例对T实例的弱引用
 * 垃圾收集器在某个时间点确定对象是弱可达的，(Reference.clear() 引用对象直接置位null，然后被回收)
 * 它将原子地清除对该对象的所有弱引用，以及对任何其他弱可达对象的所有弱引用
 * <p>
 * 4:虚引用: PhantomReference<T> 实例实例对T实例的弱引用
 * 虚引用PhantomReference<T>不能访问实例T，只能接收回收后结果
 * DirectByteBuffer类的对象回收的时候 -> 进行堆外内存的释放
 *
 * https://zhuanlan.zhihu.com/p/77357666
 *
 * @date 2020-05-09.
 */
public class ReferenceSample {
    public static void main(String[] args) throws InterruptedException {

        phantomReference();
    }

    /**
     * 虚引用
     *
     * 对于Cleaner类型（继承自虚引用）的对象会有额外的处理：在其指向的对象被回收时，
     * 会调用clean方法，该方法主要是用来做对应的资源回收，在堆外内存DirectByteBuffer中就是用Cleaner进行堆外内存的回收，这也是虚引用在java中的典型应用
     *
     * 参考"java.nio.DirectByteBuffer#Cleaner"的实现
     * @throws InterruptedException
     */
    private static void phantomReference() throws InterruptedException {
        Object obj = new Object();
        ReferenceQueue<Object> refQueue = new ReferenceQueue<>();
        PhantomReference<Object> phanRef = new PhantomReference<>(obj, refQueue);

        Object objg = phanRef.get();
        //这里拿到的是null
        System.out.println(objg);
        //让obj变成垃圾
        obj = null;
        System.gc();
        //gc后会将phanRef加入到refQueue中
        Reference<?> phanRefP = refQueue.remove();
        //这里输出true
        System.out.println(phanRefP == phanRef);
    }
}