package org.self.learn.design.pattern.creator;

/**
 * 抽象工厂
 * <p>
 * 抽象工厂顾名思义就是对工厂的抽象，
 * 例如有了卡车工厂后，自行车工厂，摩托车工厂,不同的工厂可以造drive的产品...
 * <p>
 * 现在要由具体的厂商来造能跑的车
 * <p>
 * 实际意义:
 * 不同的厂商实现相同的功能或者协议是时引入，这些功能的效果，再具体的产品里体现
 *
 * @datetime 2020-04-17 02:11.
 */
public class AbstractFactorySample {
    public static void main(String[] args) {
        Factory bikeFactory = new BikeFactory();
        Product bikeProduct = bikeFactory.create("MiniBikeProduct");
        bikeProduct.drive();

        MotorFactory motorFactory = new MotorFactory();
        Product motorProduct = motorFactory.create("LargeMotorProduct");
        motorProduct.drive();
    }
}

class BikeFactory implements Factory {

    @Override
    public Product create(String productLine) {
        if (productLine.equals("MiniBikeProduct")) {
            return new MiniBikeProduct();
        } else if (productLine.equals("LargeBikeProduct")) {
            return new LargeBikeProduct();
        } else {
            return null;
        }
    }

    class MiniBikeProduct implements Product {

        @Override
        public void drive() {
            System.out.println("MiniBikeProduct run.");
        }
    }

    class LargeBikeProduct implements Product {

        @Override
        public void drive() {
            System.out.println("LargeBikeProduct run.");
        }
    }
}

class MotorFactory implements Factory {

    @Override
    public Product create(String productLine) {
        if (productLine.equals("MiniMotorProduct")) {
            return new MiniMotorProduct();
        } else if (productLine.equals("LargeMotorProduct")) {
            return new LargeMotorProduct();
        } else {
            return null;
        }
    }

    class MiniMotorProduct implements Product {

        @Override
        public void drive() {
            System.out.println("MiniMotorProduct run.");
        }
    }

    class LargeMotorProduct implements Product {

        @Override
        public void drive() {
            System.out.println("LargeMotorProduct run.");
        }
    }
}

interface Factory {
    Product create(String productLine);
}

interface Product {
    void drive();
}