print("begin")

print("LUA_PATH", package.path)
print("LUA_CPATH", package.cpath)

hr = require"hotrodwrapper"

--hr.start()
print("get", hr.get("default", "hoge"))
hr.put("default", "hoge", "fooooooooooo")
print("get", hr.get("default", "hoge"))
print("get", hr.get("default", "HOGE"))
--hr.stop()

print("end")
