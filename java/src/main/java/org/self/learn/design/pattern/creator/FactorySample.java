package org.self.learn.design.pattern.creator;

/**
 * 卡车工厂模式:
 * 1, 对外通过工厂来出产品，避免了用户直接从制造商将车开走
 * 2, 工厂拿到的车是如何生产的细节，已被屏蔽，是为了保护制造商的产权
 * 3, 对用户来说，只需要知道买的车品牌，从工厂出车就可以了
 * <p>
 * 工厂模式实际意义:
 * 统一由工厂出产品，便于管理，而产品的实现细节，下放到不同的生产线（子类），
 * 而产品的内部优化与配置升级对于用户来说无需知道，
 * 用户只需要简单的面对工厂和最终产品即可（对于调用方，屏蔽了内部细节）
 *
 * @author Jiahao Li
 * @datetime 2020-04-17 00:38.
 */
class FactorySample {
    public static void main(String[] args) {
        CarFactory carFactory = new CarFactoryImpl();
        CarProduct carProduct = carFactory.create("audi");
        carProduct.drive();
    }

    /**
     * 卡车工厂接口(用来造卡车)
     */
    interface CarFactory {
        CarProduct create(String type);
    }

    /**
     * 具体卡车工厂(实际造卡车, 有两条生产线, 不用生产线造不同的车)
     */
    static class CarFactoryImpl implements CarFactory {

        @Override
        public CarProduct create(String productLine) {
            if (productLine.equals("audi")) {
                //制造商
                return new AudiCarProduct(200d);
            } else if (productLine.equals("benz")) {
                //制造商
                return new BenzCarProduct(150d);
            } else {
                return null;
            }
        }
    }

    /**
     * 汽车产品
     */
    interface CarProduct {
        void drive();
    }

    /**
     * 奔驰车
     */
    static class BenzCarProduct implements CarProduct {

        private Double speed;

        BenzCarProduct(Double speed) {
            this.speed = speed;
        }

        @Override
        public void drive() {
            System.out.println(String.format("Benz speed: %s drive away.", speed));
        }
    }

    /**
     * 奥迪车
     */
    static class AudiCarProduct implements CarProduct {

        private Double speed;

        AudiCarProduct(Double speed) {
            this.speed = speed;
        }

        @Override
        public void drive() {
            System.out.println(String.format("Audi speed: %s drive away.", speed));
        }
    }
}

