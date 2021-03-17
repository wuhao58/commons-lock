package com.leo.commons.redis;


import java.util.List;

/**
 * @author : LEO
 * @Description : 执行lua脚本
 * @Date :  2019/11/4
 */
public interface RedisLuaService {

    /**
     * lua脚本执行
     * @param key
     * @param luaName
     * @param params
     * @return
     */
    List<Object> execute(String luaName, String key, Object... params);

    /**
     * lua脚本执行
     * @param luaName
     * @param keys
     * @param params
     * @return
     */
    List<Object> execute(String luaName, List<String> keys, Object... params);

    /**
     * 加载lua脚本
     * @param luaNames
     * @return
     */
    void loadLuaScript(List<String> luaNames);

}
