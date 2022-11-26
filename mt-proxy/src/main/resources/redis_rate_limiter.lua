local tokens_key = KEYS[1]
local timestamp_key = KEYS[2]

local rate = tonumber(ARGV[1]) -- how many token per second allowed e.g. 100/second

local capacity = tonumber(ARGV[2]) -- bursting rate e.g. 200/second

local now = tonumber(ARGV[3]) -- current timestamp in unix time

local fill_time = capacity/rate
local ttl = math.floor(fill_time*2)

local last_tokens = tonumber(redis.call("get", tokens_key))
if last_tokens == nil then
  last_tokens = capacity
end

local last_refreshed = tonumber(redis.call("get", timestamp_key))
if last_refreshed == nil then
  last_refreshed = 0
end

local delta = math.max(0, now-last_refreshed) -- get time elapse since last fill
local filled_tokens = math.min(capacity, last_tokens+(delta*rate)) -- calc how much token should be there
local allowed = filled_tokens >= 1
local new_tokens = filled_tokens
if allowed then
  new_tokens = filled_tokens-1
end

redis.call("setex", tokens_key, ttl, new_tokens)
redis.call("setex", timestamp_key, ttl, now)

return { allowed, new_tokens }