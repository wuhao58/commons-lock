package com.leo.commons.lock.exceptions;

/**
 * @author: LEO
 * @description: 加锁失败非告警异常
 * @date: 2020-09-09 22:32
 */
public class LockFailUnNotifyException extends RuntimeException {

    public LockFailUnNotifyException(String message) {
        super(message);
    }

}
