package org.self.learn.bean;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * comment
 */
public class ApplicationContext {
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application.xml")) {
//            System.out.println(context.getBean("di", DependencyInject.class));
//            System.out.println(context.getBean("di1", DependencyInject.class));
//            System.out.println(context.getBean(DependencyInject.class));
//            System.out.println(context.getBean(BeanInitialization.class));
            System.out.println(context.getBean(BeanInitialization.class));
//            System.out.println(context.getBean(Foo.class));
        }
    }
}
