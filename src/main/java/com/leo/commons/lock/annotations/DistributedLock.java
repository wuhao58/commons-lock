package com.leo.commons.lock.annotations;

import java.lang.annotation.*;

/**
 * @author: LEO
 * @description: 分布式锁
 * @date: 2020-09-09 22:22
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * key 前缀, 必填
     */
    String key();

    /**
     * id 默认取第一个对象，否则指定变量名
     * EL表达式配置
     */
    String id() default "";

    /**
     * 锁标示
     * 默认UUID，可指定业务主键
     */
    String value() default "";

    /**
     * 过期时间，单位毫秒
     */
    long expire() default 1000;

    /**
     * 超时时间，大于0则默认等待，单位毫秒
     */
    long timeout() default 0;

    /**
     * 加锁失败是否告警 默认告警
     * @return
     */
    boolean lockFailNotify() default true;

}
