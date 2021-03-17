package com.leo.commons.lock;

/**
 * @author: LEO
 * @description:
 * @date: 2020-09-10 09:15
 */
public interface LockService {

    /**
     * 加锁，不等待
     *
     * @param key       key
     * @param value     唯一值，确定锁属于自己
     * @return 是否成功获取锁(锁超时默认1秒)
     */
    boolean lock(String key, String value);

    /**
     * 加锁，不等待
     *
     * @param key       key
     * @param value     唯一值，确定锁属于自己
     * @param expire    锁超时时间，单位毫秒
     * @return 是否成功获取锁
     */
    boolean lock(String key, String value, long expire);

    /**
     * 加锁，会等待
     *
     * @param key       key
     * @param value     唯一值，确定锁属于自己
     * @param timeout   锁最大等待时间，单位毫秒
     * @return 是否成功获取锁(锁超时默认1秒)
     */
    boolean tryLock(String key, String value, long timeout);

    /**
     * 加锁，会等待
     *
     * @param key       key
     * @param value     唯一值，确定锁属于自己
     * @param timeout   锁最大等待时间，单位毫秒
     * @param expire    锁超时时间，单位毫秒
     * @return 是否成功获取锁
     */
    boolean tryLock(String key, String value, long timeout, long expire);

    /**
     * 解锁
     * @param key       key
     * @param value     唯一值，确定锁属于自己
     */
    void unlock(String key, String value);

}
