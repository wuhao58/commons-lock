package com.leo.commons.lock.config;

import com.leo.commons.lock.LockService;
import com.leo.commons.lock.aspect.RedisLockAspect;
import com.leo.commons.lock.support.RedisLockServiceImpl;
import com.leo.commons.redis.RedisLuaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author: LEO
 * @description:
 * @date: 2020-09-09 22:37
 */
@Configuration
public class LockConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public LockService redisLockService(RedisTemplate<String, String> redisTemplate,
                                        RedisLuaService redisLuaService) {
        return new RedisLockServiceImpl(redisTemplate, redisLuaService, applicationName);
    }

    @Bean
    public RedisLockAspect redisLockAspect(LockService lockService) {
        return new RedisLockAspect(lockService);
    }

}
