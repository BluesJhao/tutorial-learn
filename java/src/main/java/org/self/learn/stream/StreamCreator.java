package org.self.learn.stream;

import java.util.Arrays;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Stream: 支持串行及并行的聚合操作的元素序列
 * <p>
 * 通过指定元素、指定数组、指定Collection创建Stream；
 * 通过Supplier创建Stream，可以是无限序列；
 * 通过其他类的相关方法创建。
 *
 * @author Jiahao Li
 * @datetime 2020-04-13 14:52.
 */
public class StreamCreator {

    public static void main(String[] args) {
        //stream创建[静态方法of]
        Stream<String> stringStream = Stream.of("A1", "A2", "A4", "A6", "A5");
        stringStream.forEach(System.out::println);

        //stream创建[基于集合构建]
        List<String> strings = com.sun.tools.javac.util.List.of("B1", "B2", "B4", "B6", "B5");
        Stream<String> stream = strings.stream();
        stream.forEach(System.out::println);

        //stream创建[基于Supplier]这种Stream保存的不是元素，而是算法，将产生无限序列
        //对于无限序列，如果直接调用forEach()或者count()这些最终求值操作，会进入死循环
        Stream<Integer> generate = Stream.generate(new SupplierSample());
        generate.limit(10).forEach(System.out::println);

        //基于基本类型提供的三个Stream接口[IntStream,LongStream,DoubleStream], 防止频繁的拆装箱
        IntStream streamInt = Arrays.stream(new int[]{1, 2, 3, 4});
        System.out.println(streamInt.average().orElse(-1));
    }

    static class SupplierSample implements Supplier<Integer> {

        private int anInt = 1;

        /**
         * Gets a result.
         *
         * @return a result
         */
        @Override
        public Integer get() {
            return anInt++;
        }
    }
}


/*编写一个能输出斐波拉契数列（Fibonacci）的LongStream 输出1, 1, 2, 3, 5, 8, 13, 21, 34, ...*/
class LongStreamSample {

    public static void main(String[] args) {
        LongStream generate = LongStream.generate(new LongSupplierSample());
        generate.limit(100).forEach(System.out::println);
    }

    static class LongSupplierSample implements LongSupplier {

        private long aLong = 1;

        /**
         * Gets a result.
         *
         * @return a result
         */
        @Override
        public long getAsLong() {
            return getNum(aLong++);
        }

        private long getNum(long n) {
            if (n == 1 || n == 2) {
                return 1;
            }
            return getNum(n - 1) + getNum(n - 2);
        }
    }
}
