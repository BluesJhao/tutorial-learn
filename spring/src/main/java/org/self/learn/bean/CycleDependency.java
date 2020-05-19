package org.self.learn.bean;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.stereotype.Component;

/**
 * spring解决循环依赖的过程
 * <p>
 * 构造器的循环依赖，spring没有解决
 * 解决了Setter/属性的循环依赖
 * <p>
 * spring单例对象的初始化大略分为三步：
 * <p>
 * createBeanInstance：实例化，其实也就是调用对象的构造方法实例化对象
 * populateBean：填充属性，这一步主要是多bean的依赖属性进行填充
 * initializeBean：调用spring xml中的init 方法。
 * 链接：https://juejin.im/post/5c98a7b4f265da60ee12e9b2
 *
 * @date 2020-05-20.
 * @see DefaultSingletonBeanRegistry
 * @see AbstractAutowireCapableBeanFactory
 * #doCreateBean#addSingletonFactory
 */
public class CycleDependency {
    public static void main(String[] args) {

    }
}

@Component
class A {

    @Autowired
    B b;

    //    @Autowired 循环依赖通过构造器会有报错
    //    public A(B b) {
    //        this.b = b;
    //    }

    @Override
    public String toString() {
        return "A{" +
                "b=" + b +
                '}';
    }
}

@Component
class B {

    @Autowired
    A a;

    //    @Autowired 循环依赖通过构造器会有报错
    //    public B(A a) {
    //        this.a = a;
    //    }
}


