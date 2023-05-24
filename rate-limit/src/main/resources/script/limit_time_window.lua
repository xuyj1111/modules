-- Lua 中 KEYS 和 ARGV 数组都是以下标 1 开始的
local key = KEYS[1]
-- Lua 需要显示的转换类型，接收都是 string
local windowTime = tonumber(ARGV[1])
local times = tonumber(ARGV[2])
-- 获取时间戳，需要注意：redis返回的两个值分别为 秒数 和 当前秒的微秒数，并且向下取整 去除小数
local timestamp = redis.call("time")
local now = math.floor(timestamp[1] * 1000 + timestamp[2] / 1000)
-- 过期时间，即上个窗口结束时间
local expired = now - windowTime

-- 删除过期时间的数据
redis.call("ZREMRANGEBYSCORE", key, 0, expired)
-- 下一个的数量
local next = tonumber(redis.call('zcard', key)) + 1

if next > times then
    for key, value in pairs(redis.call("ZRANGEBYSCORE", key, "-inf", "+inf", "LIMIT", "0", "1")) do
        return tonumber(value)
    end
else
    redis.call("zadd", key, now, now)
    redis.call("pexpire", key, windowTime)
    return (0 - next)
end