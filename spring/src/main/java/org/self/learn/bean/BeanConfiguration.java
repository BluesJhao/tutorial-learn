package org.self.learn.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Bean配合@Configuration使用
 * @Component作用在类上
 * @Bean作用在方法上，bean更加灵活，适用于创建第三方Bean给Ioc管理的时候
 *
 * 都需要通过component-scan扫描
 *
 * @date 2020-05-19.
 */
@Configuration
public class BeanConfiguration {

//    @Bean(name = "bean0", initMethod = "init", destroyMethod = "destroy")
    public BeanInitialization dependent(){
        //比如第三方对象DataSource...
        return new BeanInitialization();
    }
}
