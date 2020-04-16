package org.self.learn.design.pattern.creator;

/**
 * 静态工厂:
 * <p>
 * 在工厂模式方法之上, 不通过建厂，直接通过制造商造车，使得产品出厂的链路变短，以达到提高效率的目的
 * <p>
 * 实际意义:
 * 不需要实例化工厂即可快速出产品，Java源码随处可见，应用极广
 * <p>
 * 演变到自己是工厂，自己又是产品，一手操办
 * Integer.valueOf(), Long.valueOf() ...
 *
 * @author Jiahao Li
 * @datetime 2020-04-17 01:37.
 */
public class StaticFactorySample {

    private static FactorySample.CarProduct getCarInstance(String productLine) {
        if (productLine.equals("audi")) {
            //制造商
            return new FactorySample.AudiCarProduct(200d);
        } else if (productLine.equals("benz")) {
            //制造商
            return new FactorySample.BenzCarProduct(150d);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        FactorySample.CarProduct carProduct = StaticFactorySample.getCarInstance("audi");
        assert carProduct != null;
        carProduct.drive();

        Object singleInstance = SingleFactory.getSingleInstance();
        System.out.println(singleInstance.toString());

        final Integer integer = Integer.valueOf("123");
        System.out.println(integer);
    }

    static class SingleFactory {

        private static Object single = null;

        private SingleFactory() {
        }

        static Object getSingleInstance() {
            if (single == null) {
                synchronized (SingleFactory.class) {
                    if (single == null) {
                        single = new SingleFactory();
                    }
                }
            }
            return single;
        }
    }
}

