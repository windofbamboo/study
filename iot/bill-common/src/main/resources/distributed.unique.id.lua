-- get next sequence
-- User: taosy,shanzq
-- Date: 17/11/17
---------------------------------------------------------------------------
-- keys=DUID's key,ARGVs=init-value,incr_amount,DUID's length
---------------------------------------------------------------------------
-- redis禁止在spop、srandmember、sscan、zscan、hscan、randomkey、scan、lastsave、pubsub、time之后调用写命令
-- 所以需要加入下面的代码
-- 执行了redis.replicate_commands()之后，Redis就开始使用multi/exec来包围Lua脚本中调用的写命令
redis.replicate_commands();
-- 序列的key
local key = tostring(KEYS[1]);
-- 初始化序列后缀值
local init_value = tonumber(ARGV[1]);
-- 自增长数
local incr_amount = tonumber(ARGV[2]);
-- 序列长度
local length = tonumber(ARGV[3]);
-- 序列是否按天
local valuemode = tonumber(ARGV[4]);
-- 时区，单位：小时，默认为8小时
local timezone = tonumber(ARGV[5]);
-- 获取已存在的序列
local seq = redis.call('get', key)
-- 将时间转换成YYYYMMDDHHMISS
local function toYYYYMMDDHHMISS(t)
  local day_sec = 24 * 60 * 60;
  local year = 1970;
  local month;
  local day;
  local hour;
  local minute;
  local second;
  local mons = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

  local now = t+timezone*60*60;
  -- 获取年份
  local days = now/day_sec;
  local curr_day;
  if year % 4 == 0 and (year % 100 ~= 0 or year % 400 == 0) then
    curr_day = 366
  else
    curr_day = 365
  end
  while(days >= curr_day)
  do
   days = days - curr_day;
   year = year + 1;
   if year % 4 == 0 and (year % 100 ~= 0 or year % 400 == 0) then
     curr_day = 366
   else
     curr_day = 365
   end
  end
  day = days;

  if year % 4 == 0 and (year % 100 ~= 0 or year % 400 == 0) then
    mons[1] = mons[1] + 1;
  end
  -- 获取月份和天数
  for i,v in ipairs(mons) do
    if day < v then
      month = i;
      day = day + 1;
      break;
    end
    day = day - v;
  end
  -- 剩余天数
  local sec_in_day = math.floor(now % day_sec);
  hour = sec_in_day / (60 * 60);
  minute = sec_in_day % (60 * 60) / 60;
  second = sec_in_day % 60;
  return {year=year, month=month, day=day, hour=hour,minute=minute,second=second}  
end
if string.len(seq) < length then
  -- 拼接时间
  local YYYYMMDDHHMISS=toYYYYMMDDHHMISS(tonumber(redis.call('TIME')[1]))
  local seq_prefix =  string.sub(YYYYMMDDHHMISS.year, 3, -1) ..
                      string.format('%02d', YYYYMMDDHHMISS.month) ..
                      string.format('%02d', YYYYMMDDHHMISS.day) ..
                      string.format('%02d', YYYYMMDDHHMISS.hour) ..
                      string.format('%02d', YYYYMMDDHHMISS.minute) ..
                      string.format('%02d', YYYYMMDDHHMISS.second);
  seq = seq_prefix .. string.format('%0' .. (length - 12) .. 'd', init_value);
  -- redis.log(redis.LOG_NOTICE,'reset->key:' .. key .. ',seq:' .. seq .. ',init_value:' .. init_value);
  redis.call('set', key, seq);
  return seq;
else
  if valuemode == 1 then
    --id换天
    local YYYYMMDDHHMISS=toYYYYMMDDHHMISS(tonumber(redis.call('TIME')[1]))
    local seqdate=string.sub(seq,1,6)
    local nowdate=string.sub(YYYYMMDDHHMISS.year, 3, -1) ..
                      string.format('%02d', YYYYMMDDHHMISS.month) ..
                      string.format('%02d', YYYYMMDDHHMISS.day)
    if tonumber(seqdate) < tonumber(nowdate) then
        local seq_prefix =  string.sub(YYYYMMDDHHMISS.year, 3, -1) ..
                      string.format('%02d', YYYYMMDDHHMISS.month) ..
                      string.format('%02d', YYYYMMDDHHMISS.day) ..
                      string.format('%02d', YYYYMMDDHHMISS.hour) ..
                      string.format('%02d', YYYYMMDDHHMISS.minute) ..
                      string.format('%02d', YYYYMMDDHHMISS.second);
        seq = seq_prefix .. string.format('%0' .. (length - 12) .. 'd', init_value);
        -- redis.log(redis.LOG_NOTICE,'resetday->key:' .. key .. ',seq:' .. seq .. ',init_value:' .. init_value);
        redis.call('set', key, seq);
        return seq;
    end
  end
  -- 此处返回用redis.call('get', key)，redis的incrby可能返回科学计数法
  redis.call('incrby', key, incr_amount);
  -- redis.log(redis.LOG_NOTICE,'incrby->seq:' .. seq)
  return redis.call('get', key);
end


