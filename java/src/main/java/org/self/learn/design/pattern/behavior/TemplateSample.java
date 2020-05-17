package org.self.learn.design.pattern.behavior;

import java.util.HashMap;
import java.util.Map;

/**
 * 由父类定义一个流程骨架，将一部分实现延迟到子类实现
 *
 * @date 2020-04-30.
 */
public class TemplateSample {

    public static void main(String[] args) {
        AbstractConfiguration cfg = new RedisConfiuration();
        String value = cfg.getConfig("hello");
        System.out.println(value);
    }

    static abstract class AbstractConfiguration {
        Map<String, String> map = new HashMap<>();

        final String getConfig(String key){
            String conf = map.get(key);
            if (conf == null) {
                return readFromDB(key);
            }
            return conf;
        }

        abstract String readFromDB(String key);
    }

    class MySQLConfiuration extends AbstractConfiguration{

        @Override
        String readFromDB(String key) {
            return "select value from MySQL : " + key;
        }
    }

    static class RedisConfiuration extends AbstractConfiguration{

        @Override
        String readFromDB(String key) {
            return "select value from Redis : " + key;
        }
    }
}
