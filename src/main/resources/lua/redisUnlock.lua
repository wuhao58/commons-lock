---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by LEO.
--- DateTime: 2019/12/25 14:11
---
if (redis.call('get', KEYS[1]) == ARGV[1])
then
    return redis.call('del', KEYS[1])
else
    return -1
end