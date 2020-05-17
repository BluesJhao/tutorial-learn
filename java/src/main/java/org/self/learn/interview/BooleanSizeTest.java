package org.self.learn.interview;

/**
 * Java中boolean占用的大小是不明确的
 * 当前例子为了比对boolean类型数据在数组中所占用的大小与int类型数据在数组中的大小
 *
 * 执行结果相差四倍: 印证boolean在数组中占用1个字节
 *
 * Size for LotsOfBooleans: 8270456
 * Average size: 82.70456
 * Size for LotsOfInts: 33599984
 * Average size: 335.99984
 * 0
 *
 * 从多方资料中查看到：在标准的jvm规范实现中：
 * 1: boolean在数组中的类型占用1个字节，编译后在jvm中使用byte[]的操作指令
 * 2: boolean占用4个字节，编译后在jvm中使用int类型表示
 *
 * 引用: https://blog.csdn.net/XiaHeShun/article/details/79634232
 *
 * 其他: 早期32位的cpu与jvm中的int(占用32位)相匹配， 这也是boolean采用int的原因
 *
 * 结论: boolean可能占用1个或4个字节，取决于jvm是否按标准规范来实现的
 *
 * @date 2020-04-29.
 */
public class BooleanSizeTest {
    static class LotsOfBooleans {
        boolean a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, aa, ab, ac, ad, ae, af;
        boolean b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, ba, bb, bc, bd, be, bf;
        boolean c0, c1, c2, c3, c4, c5, c6, c7, c8, c9, ca, cb, cc, cd, ce, cf;
        boolean d0, d1, d2, d3, d4, d5, d6, d7, d8, d9, da, db, dc, dd, de, df;
        boolean e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, ea, eb, ec, ed, ee, ef;
    }

    static class LotsOfInts {
        int a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, aa, ab, ac, ad, ae, af;
        int b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, ba, bb, bc, bd, be, bf;
        int c0, c1, c2, c3, c4, c5, c6, c7, c8, c9, ca, cb, cc, cd, ce, cf;
        int d0, d1, d2, d3, d4, d5, d6, d7, d8, d9, da, db, dc, dd, de, df;
        int e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, ea, eb, ec, ed, ee, ef;
    }


    private static final int SIZE = 100000;

    public static void main(String[] args) {
        LotsOfBooleans[] first = new LotsOfBooleans[SIZE];
        LotsOfInts[] second = new LotsOfInts[SIZE];

        System.gc();
        long startMem = getMemory();

        for (int i = 0; i < SIZE; i++) {
            first[i] = new LotsOfBooleans();
        }

        System.gc();
        long endMem = getMemory();

        System.out.println("Size for LotsOfBooleans: " + (endMem - startMem));
        System.out.println("Average size: " + ((endMem - startMem) / ((double) SIZE)));

        System.gc();
        startMem = getMemory();
        for (int i = 0; i < SIZE; i++) {
            second[i] = new LotsOfInts();
        }
        System.gc();
        endMem = getMemory();

        System.out.println("Size for LotsOfInts: " + (endMem - startMem));
        System.out.println("Average size: " + ((endMem - startMem) / ((double) SIZE)));

        // Make sure nothing gets collected
        long total = 0;
        for (int i = 0; i < SIZE; i++) {
            total += (first[i].a1 ? 1 : 0) + second[i].a1;
        }
        System.out.println(total);
    }

    private static long getMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
