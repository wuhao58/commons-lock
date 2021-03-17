package com.leo.commons.redis.config;

import com.leo.commons.redis.RedisLuaService;
import com.leo.commons.redis.impl.RedisLuaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author: LEO
 * @description: lettuce自动装配
 * @date: 2020-09-09 21:10
 * @since:
 */
@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class LettuceAutoConfiguration {

    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;

    /**
     * RedisTemplate配置
     *
     * @return
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        // 配置redisTemplate
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisLuaService redisLuaService() {
        return new RedisLuaServiceImpl(redisTemplate());
    }

}
