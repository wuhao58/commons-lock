package com.leo.commons.lock.exceptions;

/**
 * @author: LEO
 * @description: 加锁失败告警异常
 * @date: 2020-09-09 22:32
 */
public class LockFailNotifyException extends RuntimeException {

    public LockFailNotifyException(String message) {
        super(message);
    }

}
