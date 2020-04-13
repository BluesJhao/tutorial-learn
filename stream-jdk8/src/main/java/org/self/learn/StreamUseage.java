package org.self.learn;

import com.sun.tools.javac.util.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Stream的应用:
 * <p>
 * filter,map转换,reduce聚合...
 * <p>
 * reduce()方法将一个Stream的每个元素依次作用于BinaryOperator，并将结果合并
 * <p>
 * Stream提供的常用操作有：
 * <p>
 * 转换操作：map()，filter()，sorted()，distinct()；
 * <p>
 * 合并操作：concat()，flatMap()；
 * <p>
 * 并行处理：parallel()；
 * <p>
 * 聚合操作：reduce()，collect()，count()，max()，min()，sum()，average()；
 * <p>
 * 其他操作：allMatch(), anyMatch(), forEach()。
 *
 * @author Jiahao Li
 * @datetime 2020-04-13 17:06.
 */
public class StreamUseage {

    public static void main(String[] args) {

        List<String> stringList = List.of("a1", " a2", "a3", "a4", "a5 ");
        String a5 = stringList.stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.joining(", "));
        System.out.println(a5);

        //Stream转集合
        final java.util.Set<String> collect = Stream.of("A", "B", "C", "D", "C").collect(Collectors.toSet());
        System.out.println(collect);

        //Stream转数组
        final int[] ints = IntStream.of(1, 2, 3).toArray();

        //Stream转MaP
        final Stream<String> mapStream = Stream.of("name:foo", "age:21");
        final Map<String, String> stringMap = mapStream.collect(Collectors.toMap(t -> t.split(":")[0], t -> t.split(":")[1]));
        stringMap.forEach((K, V) -> System.out.println(K + "=" + V));

        //分组归类
        List<String> list = List.of("Apple", "Banana", "Blackberry", "Coconut", "Avocado", "Cherry", "Apricots", "Banana");
        final Map<String, java.util.List<String>> listMap = list.stream().collect(Collectors.groupingBy(t -> t.substring(0, 1)));
        System.out.println(listMap);

        //排序
        list.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()).forEach(System.out::println);

        //去重
        list.stream().distinct().skip(2).limit(5).collect(Collectors.toList()).forEach(System.out::println);

        //Stream提供连接
        Stream<String> s11 = List.of("AA", "BB", "CC").stream();
//        final Stream<String> b = s11.peek(System.out::println);
        Stream<String> s12 = List.of("DD", "EE", "BB").stream();
        Stream<String> s13 = Stream.concat(s11, s12);
        s13.forEach(System.out::println);

        //连接多集合操作
        final Stream<java.util.List<String>> listStream = Stream.of(
                Arrays.asList("A1", "A2"),
                Arrays.asList("B1", "B2"),
                Arrays.asList("C1", "C2")
        );
        listStream.flatMap(Collection::stream).forEach(System.out::println);

        //并行操作
        final List<String> strings = List.of("Q", "Y", "W", "E", "R");
        strings.parallelStream().sorted().collect(Collectors.toList()).forEach(System.out::println);

        //计算基数
        IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .filter(n -> n % 2 != 0)
                .forEach(System.out::println);

        //计算之和[identity为初始值]
        int reduce = IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).reduce(0, Integer::sum);
        System.out.println(reduce);

        //关注集合之间的执行顺序，聚合类操作立即执行，其余lazy
        Stream<Long> s1 = Stream.generate(new NatualSupplier());
        Stream<Long> s2 = s1.map(n -> n * n);
        Stream<Long> s3 = s2.map(n -> n - 1);
        Stream<Long> s4 = s3.limit(3);
        System.out.println(s4.reduce((long) 0, Long::sum));

        //逐行迭代文件[遍历文本文件]
        try (Stream<String> lines = Files.lines(Paths.get("pom.xml"))) {
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //读取properties并聚合
        try (Stream<String> stringStream = Files.lines(Paths.get("stream-jdk8/src/main/resources/test.properties"))) {
            stringStream
                    .filter(t -> !Objects.isNull(t) && t.length() > 0)
                    .map(t -> {
                        final String[] split = t.split("=");
                        final HashMap<Object, Object> map = new HashMap<>();
                        if (split.length > 0) {
                            final String key = split[0] != null ? split[0].trim() : null;
                            String value;
                            if (split.length < 2) {
                                value = null;
                            } else {
                                value = split[1].trim();
                            }
                            map.put(key, value);
                        }
                        return map;
                    }).reduce(new HashMap<>(), (x, y) -> {
                x.putAll(y);
                return x;
            }).forEach((x, y) -> System.out.println(x + "=" + y));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class LocalDateSample {
    public static void main(String[] args) {
        String[] array = new String[]{" 2019-12-26 ", "2020 - 01-09 ",
                "2020- 05 - 01 ", "2022 - 02 - 01", " 2025-01 -01"};
        Arrays.stream(array)
                .map(String::trim)
                .map(t -> t.replaceAll(" ", ""))
                .map(t -> LocalDate.parse(t, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .forEach(System.out::println);
    }
}

class NatualSupplier implements Supplier<Long> {
    private long n = 0;

    public Long get() {
        n++;
        return n;
    }
}


