package org.self.learn.thread;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 线程执行完成时，进行回调，不会阻塞主线程
 * <p>
 * Feature的get操作获取结果会阻塞主线程
 * <p>
 * 异步任务结束时，会自动回调某个对象的方法:
 * 异步任务出错时，会自动回调某个对象的方法:
 * 主线程设置好回调后，不再关心异步任务的执行。
 *
 * CompletableFuture可以指定异步处理流程:
 * thenAccept()处理正常结果；
 * exceptional()处理异常结果；
 * thenApplyAsync()用于串行化另一个CompletableFuture；
 * anyOf()和allOf()用于并行化多个CompletableFuture。
 *
 * @date 2020-05-18.
 */
public class CompletableFutureSample {
    public static void main(String[] args) throws Exception {
        // 1: 创建异步执行任务:
        Supplier<Double> fetchPrice = CompletableFutureSample::fetchPrice;
        // 需要提供supplier
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(fetchPrice);
        // 如果执行成功:
        cf.thenAccept((result) -> System.out.println("price: " + result));
        // 如果执行异常:
        cf.exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
        System.out.println("Non-blocking");
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
        Thread.sleep(3000);


        // 2: 第一个任务:
        CompletableFuture<String> cfQuery = CompletableFuture.supplyAsync(() -> {
            return queryCode("中国石油");
        });
        // cfQuery成功后继续执行下一个任务:
        CompletableFuture<Double> cfFetch = cfQuery.thenApplyAsync((code) -> {
            return fetchPrice(code);
        });
        // cfFetch成功后打印结果:
        cfFetch.thenAccept((result) -> {
            System.out.println("cascade price: " + result);
        });
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
        Thread.sleep(10000);


        // 3: 两个CompletableFuture执行异步查询:
        System.out.println("starting ...");
        CompletableFuture<String> cfQueryFromSina = CompletableFuture.supplyAsync(() -> {
            return queryCode("中国石油", "https://finance.sina.com.cn/code/");
        });
        CompletableFuture<String> cfQueryFrom163 = CompletableFuture.supplyAsync(() -> {
            return queryCode("中国石油", "https://money.163.com/code/");
        });
        // 用anyOf合并为一个新的CompletableFuture:
        CompletableFuture<Object> cfQuery0 = CompletableFuture.anyOf(cfQueryFromSina, cfQueryFrom163);

        // 两个CompletableFuture执行异步查询:
        CompletableFuture<Double> cfFetchFromSina = cfQuery0.thenApplyAsync((code) -> {
            return fetchPrice((String) code, "https://finance.sina.com.cn/price/");
        });
        CompletableFuture<Double> cfFetchFrom163 = cfQuery0.thenApplyAsync((code) -> {
            return fetchPrice((String) code, "https://money.163.com/price/");
        });
        // 用anyOf合并为一个新的CompletableFuture:
        CompletableFuture<Object> cfFetch0 = CompletableFuture.anyOf(cfFetchFromSina, cfFetchFrom163);
        // 最终结果:
        cfFetch0.thenAccept((result) -> {
            System.out.println("price: " + result);
        });
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
        Thread.sleep(50000);
    }

    static String queryCode(String name, String url) {
        try {
            Thread.sleep((long) (Math.random() * 10000));
        } catch (InterruptedException e) {
        }
        System.out.println("query code from " + url + "...");
        return "601857";
    }

    static Double fetchPrice(String code, String url) {
        try {
            Thread.sleep((long) (Math.random() * 10000));
        } catch (InterruptedException e) {
        }
        System.out.println("query price from " + url + "...");
        return 5 + Math.random() * 20;
    }

    static String queryCode(String name) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        return "601857";
    }

    static Double fetchPrice() {
        try {
            Thread.sleep(800);
        } catch (InterruptedException ignored) {
        }
        if (Math.random() < 0.3) {
            throw new RuntimeException("fetch price failed!");
        }
        return 5 + Math.random() * 20;
    }

    static Double fetchPrice(String code) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        if (Math.random() < 0.3) {
            throw new RuntimeException("fetch price failed!");
        }
        return 5 + Math.random() * 20;
    }
}
