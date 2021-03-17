package com.leo.commons.redis.impl;

import com.leo.commons.redis.RedisLuaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : LEO
 * @Description : 执行lua脚本
 * @Date :  2019/11/4
 */
@Slf4j
public class RedisLuaServiceImpl implements RedisLuaService {

    private Map<String, RedisScript<List>> redisScriptMap = new HashMap<>();

    private RedisTemplate<String, String> redisTemplate;

    public RedisLuaServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Object> execute(String luaName, String key, Object... params) {
        return execute(luaName, Collections.singletonList(key), params);
    }

    @Override
    public List<Object> execute(String luaName, List<String> keys, Object... params) {
        RedisScript<List> redisScript = getRedisScript(luaName);
        if (null == redisScript) {
            throw new RuntimeException("RedisScript cannot be null");
        }
        return redisTemplate.execute(redisScript, keys, params);
    }

    @Override
    public void loadLuaScript(List<String> luaNames) {
        if (null != luaNames) {
            for (String luaName : luaNames) {
                getRedisScript(luaName);
                log.info("load lua script name={}", luaName);
            }
        }
    }

    public RedisScript<List> getRedisScript(String luaName) {
        if (redisScriptMap.containsKey(luaName)) {
            return redisScriptMap.get(luaName);
        }
        return initRedisScript(luaName);
    }

    public synchronized RedisScript<List> initRedisScript(String luaName) {
        try {
            String fileName = String.format("lua/%s.lua", luaName);
            ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource(fileName));
            RedisScript<List> redisScript = new DefaultRedisScript(scriptSource.getScriptAsString(), List.class);
            redisScriptMap.put(luaName, redisScript);
            return redisScript;
        } catch (IOException e) {
            log.error("load RedisScript fail, pls check the name={}", luaName);
        }
        return null;
    }

}
