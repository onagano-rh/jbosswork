hr = require "hotrodwrapper"

local uri = ngx.var.uri

local path = {};
for e in string.gmatch(uri, "[^/]+") do
    table.insert(path, e)
end

local root = path[1]
local cache = path[2]
local key = path[3]
local value = path[4]


if value then
   -- ngx.log(ngx.NOTICE, "try to put")
   local ret, err = hr.put(cache, key, value)
   if err then
      ngx.log(ngx.ERR, err)
      ngx.exit(ngx.HTTP_INTERNAL_SERVER_ERROR)
   else
      ngx.say("put " .. value .. " for " .. key)
      ngx.exit(ngx.HTTP_OK)
   end
elseif key then
   -- ngx.log(ngx.NOTICE, "try to get")
   local ret, err = hr.get(cache, key)
   if err then
      ngx.log(ngx.ERR, err)
      ngx.exit(ngx.HTTP_INTERNAL_SERVER_ERROR)
   else
      ngx.say("got " .. tostring(ret) .. " for " .. key)
      ngx.exit(ngx.HTTP_OK)
   end
else
   ngx.log(ngx.ERR, "not matched")
   ngx.exit(ngx.HTTP_BAD_REQUEST)
end
