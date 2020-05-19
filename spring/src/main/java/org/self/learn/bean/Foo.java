package org.self.learn.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * comment
 *
 * @date 2020-05-19.
 */
@Component
public class Foo {

    private String             code;
    private String             message;

    @Autowired//不推荐字段注入
    private BeanInitialization beanInitialization;

    public Foo() {
        //注入在之后
        System.out.println("cycle dependency constructor: " + beanInitialization);
    }

    public Foo(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public Foo(BeanInitialization beanInitialization) {
        this.beanInitialization = beanInitialization;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BeanInitialization getBeanInitialization() {
        return beanInitialization;
    }

    public void setBeanInitialization(BeanInitialization beanInitialization) {
        this.beanInitialization = beanInitialization;
    }

    @Override
    public String toString() {
        return "DependencyInject{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", beanInitialization=" + beanInitialization +
                '}';
    }

}
