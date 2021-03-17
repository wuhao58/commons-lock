package com.leo.commons.lock.support;

import com.leo.commons.lock.LockService;
import com.leo.commons.redis.RedisLuaService;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author: LEO
 * @description:
 * @date: 2020-09-10 09:22
 */
public class RedisLockServiceImpl implements LockService {

    private static final String LOCK_PRE = "lock";
    private static final int DEFAULT_LOCK_EXPIRE = 1000;

    private RedisTemplate<String, String> redisTemplate;
    private RedisLuaService redisLuaService;
    private String applicationName;

    public RedisLockServiceImpl(RedisTemplate<String, String> redisTemplate, RedisLuaService redisLuaService, String applicationName) {
        this.redisTemplate = redisTemplate;
        this.redisLuaService = redisLuaService;
        this.applicationName = applicationName;
    }

    @Override
    public boolean lock(@NonNull String key, @NonNull String value) {
        String lockKey = getLockKey(key);
        String lockValue = getLockValue(value);

        return toLock(lockKey, lockValue, DEFAULT_LOCK_EXPIRE);
    }

    @Override
    public boolean lock(@NonNull String key, @NonNull String value, long expire) {
        String lockKey = getLockKey(key);
        String lockValue = getLockValue(value);

        return toLock(lockKey, lockValue, expire);
    }

    @Override
    public boolean tryLock(@NonNull String key, @NonNull String value, long timeout) {
        return tryLock(key, value, timeout, DEFAULT_LOCK_EXPIRE);
    }

    @Override
    public boolean tryLock(@NonNull String key, @NonNull String value, long timeout, long expire) {
        if (timeout > expire) {
            timeout = expire;
        }
        String lockKey = getLockKey(key);
        String lockValue = getLockValue(value);

        long end = System.currentTimeMillis() + timeout;
        while (!toLock(lockKey, lockValue, expire)) {
            if (System.currentTimeMillis() > end) {
                return false;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        return true;
    }

    @Override
    public void unlock(@NonNull String key, @NonNull String value) {
        String lockKey = getLockKey(key);
        String lockValue = getLockValue(value);

        String luaName = "redisUnlock";
        redisLuaService.execute(luaName, lockKey, lockValue);
    }

    private boolean toLock(String key, String value, long expire) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, expire, TimeUnit.MILLISECONDS);
        return (success != null && success);
    }

    private String getLockKey(String key) {
        return String.format("%s:%s:%s", applicationName, LOCK_PRE, key);
    }

    private String getLockValue(String value) {
        return String.format("%s_%s", Thread.currentThread().getId(), value);
    }

}
