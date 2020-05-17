package org.self.learn.design.pattern.behavior;

/**
 * 策略模式.
 * <p>
 * 策略模式是将类的不同的行为或者算法封装成一个策略类，通过一个上下文切换来确认使用具体的策略
 * <p>
 * 可以解决替换很多if else的场景，实则是对一个类行为抽象出去，以达到更好的扩展性
 *
 * @date 2020-04-30.
 */
public class StrategySample {
    public static void main(String[] args) {

        //对于context来说，通过具体的策略参数，执行得到该策略的结果
        Context context = new Context();

        Strategy strategy = new AddStrategy();
        context.setStrategy(strategy);
        System.out.println("Add strategy : " + context.operate(8, 23));

        strategy = new DivideStrategy();
        context.setStrategy(strategy);
        System.out.println("divide strategy : " + context.operate(23, 3));

    }

    static class Context {
        Strategy strategy;

        void setStrategy(Strategy strategy) {
            this.strategy = strategy;
        }

        Long operate(long t1, long t2) {
            return strategy.opreate(t1, t2);
        }

    }

    interface Strategy {
        Long opreate(long t1, long t2);
    }

    static class AddStrategy implements Strategy {

        @Override
        public Long opreate(long t1, long t2) {
            return t1 + t2;
        }
    }

    static class DivideStrategy implements Strategy {

        @Override
        public Long opreate(long t1, long t2) {
            return t1 / t2;
        }
    }
}
