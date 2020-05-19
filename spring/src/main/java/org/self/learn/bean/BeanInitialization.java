package org.self.learn.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 模拟Bean的初始化过程
 * <p>
 * @see DefaultSingletonBeanRegistry
 * @see AbstractAutowireCapableBeanFactory
 *  * #doCreateBean#addSingletonFactory
 *
 *  doCreateBean中的关键三部(之前根据读取配置文件得到BeanDefinition(RootBeanDefinition)来创建对象)
 *
 *  1: createBeanInstance ::对象实例化
 *     addSingletonFactory ::提前将对象暴露, 解决单例setter/field循环依赖问题
 *  2: populateBean ::对属性注入依赖, get(依赖对象)，没有则创建，重复1步骤
 *  3: initializeBean ::对象初始化
 *    3.1: invokeAwareMethods ::aware相关接口调用
 *       3.1.1: ((BeanNameAware) bean).setBeanName(beanName);
 *       3.1.2: ((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
 *       3.1.3: ((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
 *     **3.1.4**: ((ApplicationContextAware) bean).setApplicationContext(XXXX);
 *    3.2: invokeInitMethods ::调用初始化方法
 *       3.2.1: ((InitializingBean) bean).afterPropertiesSet(); ::调用afterPropertiesSet
 *       3.2.3: invokeCustomInitMethod(beanName, bean, mbd); ::调用自定义initMethod（配置的init-method）
 *
 * 备注: 以上类型转换部分均需要判断对象是否实现了相关转换接口
 *
 *       提前暴露当前接口时: 实质上执行以下逻辑, 通过存三级缓存singletonFactories，get(bean)来转移到earlySingletonObjects对外暴露
 *       if (!this.singletonObjects.containsKey(beanName)) {
 * 				this.singletonFactories.put(beanName, singletonFactory);
 * 				this.earlySingletonObjects.remove(beanName);
 * 				this.registeredSingletons.add(beanName);
 *       }
 *       //实质上通过三个缓存来完成提前暴露的
 *       private final Map<String, Object> singletonObjects=new ConcurrentHashMap<>(256); ::一级缓存
 *       private final Map<String, Object> earlySingletonObjects=new HashMap<>(16);       ::二级缓存
 *       private final Map<String, ObjectFactory<?>>singletonFactories=new HashMap<>(16); ::三级缓存
 */
@Component
public class BeanInitialization implements InitializingBean, BeanNameAware, BeanFactoryAware, ApplicationContextAware
        , BeanClassLoaderAware {

    private String message = "init";

    private String beanName;

    private BeanFactory beanFactory;

    private ApplicationContext applicationContext;

    @Autowired
    private Foo foo;

    public void setMessage(String message) {
        this.message = message;
    }

    //会造成循环调用dependencyInject，如果dependencyInject的toString也引用Dependent的话
    @Override
    public String toString() {
        return "BeanInitialization{" +
                "message='" + message + '\'' +
                ", beanName='" + beanName + '\'' +
                ", beanFactory=" + beanFactory +
                ", applicationContext=" + applicationContext +
//                ", dependencyInject=" + foo +
                '}';
    }

    //顺序4
    @PostConstruct
    private void post() {
        message = "post val";
        System.out.println("execute post...");
    }

    //如果配置了initMethod, 顺序6, Bean初始化完成并可用
    private void init() {
        message = "init val";
        System.out.println("execute init...");
    }

    //如果配置了destoryMethod, IOC容器销毁前执行
    private void destroy() {
        System.out.println("execute destroy...");
    }

    //如果实现InitializingBean, 顺序5
    @Override
    public void afterPropertiesSet() {
        message = "afterPropertiesSet val";
        System.out.println("execute afterPropertiesSet");
    }

    //如果实现BeanNameAware, 顺序1
    @Override
    public void setBeanName(String s) {
        beanName = s;
        System.out.println("execute setBeanName...");
    }

    //如果实现BeanFactoryAware, 顺序2
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        System.out.println("execute setBeanFactory...");
    }

    //如果实现ApplicationContextAware, 顺序3
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        System.out.println("execute setApplicationContext...");
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        System.out.println("execute setBeanClassLoader...");
    }
}
