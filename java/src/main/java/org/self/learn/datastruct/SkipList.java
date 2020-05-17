package org.self.learn.datastruct;

import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 跳跃表数据结构:效率比肩平衡树
 * <p>
 * 一种基于有序链表叠加多级索引层链表的一种数据结构
 * <p>
 * https://blog.csdn.net/pcwl1206/article/details/83512600
 * <p>
 * 原码深度分析:https://www.jianshu.com/p/edc2fd149255
 * <p>
 * * Head nodes          Index nodes
 * * +-+    right          +-+                      +-+
 * * |2|------------------>| |--------------------->| |
 * * +-+                   +-+                      +-+
 * *  | down                |                        |
 * *  v                     v                        v
 * * +-+              +-+  +-+       +-+            +-+       +-+
 * * |1|------------->| |->| |------>| |----------->| |------>| |
 * * +-+              +-+  +-+       +-+            +-+       +-+
 * *  v                |    |         |              |         |
 * * Nodes  next       v    v         v              v         v
 * * +---+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+
 * * |obj|->|A|->|B|->|C|->|D|->|E|->|F|->|G|->|H|->|I|->|J|->|K|
 * * +---+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+
 * <p>
 * Java8中ConcurrentSkipListMap是一种有序链表+随机索引层的一种数据结构（不确定什么时候需要新增索引层）
 *
 * 是否需要新增index及level是通过随机因子通过高低位来确定的
 *
 * @date 2020-05-07.
 */
public class SkipList {

    public static void main(String[] args) throws InterruptedException {
        //CAS无锁化竞争，支持并发新增与删除
        ConcurrentSkipListMap<Object, Object> skipListMap = new ConcurrentSkipListMap<>();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            new Thread(() -> skipListMap.put("key" + finalI, finalI)).start();
            Thread.sleep(10);
            System.out.println(skipListMap.remove("key" + (finalI - 2)));
        }
        System.out.println(skipListMap);
    }
}
