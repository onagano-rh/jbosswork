#include <stdlib.h>

#include "lua.hpp"

#include "infinispan/hotrod/ConfigurationBuilder.h"
#include "infinispan/hotrod/RemoteCacheManager.h"
#include "infinispan/hotrod/RemoteCache.h"

using namespace infinispan::hotrod;

typedef RemoteCache<std::string, std::string> StringCache;

#ifdef __cplusplus
extern "C" {
#endif
  int luaopen_hotrodwrapper(lua_State *L);
#ifdef __cplusplus
}
#endif

static RemoteCacheManager *cacheManager = NULL;

static int start(lua_State *L) {
  ConfigurationBuilder b;
  b.addServer().host("127.0.0.1").port(11222)
    .protocolVersion(infinispan::hotrod::Configuration::PROTOCOL_VERSION_12)
    .tcpNoDelay(true);
  b.connectionPool()
    .maxActive(10).minIdle(5);
  try {
    cacheManager = new RemoteCacheManager(b.build());
  }
  catch (const std::exception &e) {
    // std::cerr << "start failed: " << e.what() << std::endl;
    // return -1;
    throw e;
  }
  return 0;
}

static int stop(lua_State *L) {
  if (cacheManager != NULL) {
    try {
      cacheManager->stop();
      delete cacheManager;
      cacheManager = NULL;
    }
    catch (const std::exception &e) {
      // std::cerr << "stop failed: " << e.what() << std::endl;
      // return -1;
      throw e;
    }
  }
  return 0;
}

int main(int argc, char **argv) {
  start(NULL);
  std::cout << "started" << std::endl;
  StringCache sc = cacheManager->getCache<std::string, std::string>("default");
  std::string key = "hoge";
  std::string value = "fooooo";
  sc.put(key, value);
  std::string v, *pv;
  pv = sc.get(key);
  v = (pv != NULL) ? *pv : std::string("(NIL)");
  std::cout << "got " << v << std::endl;
  //std::cout << "got " << *sc.get("buzz") << std::endl;
  delete pv;
  stop(NULL);
  std::cout << "stopped" << std::endl;
  return 0;
}

static int get(lua_State *L) {
  const char *cache = lua_tostring(L, 1);
  const char *key = lua_tostring(L, 2);
  try {
    StringCache sc = cacheManager->getCache<std::string, std::string>(cache);
    std::auto_ptr<std::string> rv(sc.get(key));
    if (rv.get() != 0)
      lua_pushstring(L, rv->c_str());
    else
      lua_pushnil(L);
    return 1;
  }
  catch (const std::exception &e) {
    std::string em = e.what();
    // std::cerr << "get failed: " << em << std::endl;
    lua_pop(L, lua_gettop(L));
    lua_pushboolean(L, false);
    lua_pushstring(L, em.c_str());
    return 2;
  }
}

static int put(lua_State *L) {
  const char *cache = lua_tostring(L, 1);
  const char *key = lua_tostring(L, 2);
  const char *value = lua_tostring(L, 3);
  try {
    StringCache sc = cacheManager->getCache<std::string, std::string>(cache);
    sc.put(key, value);
    return 0;
  }
  catch (const std::exception &e) {
    std::string em = e.what();
    // std::cerr << "put failed: " << em << std::endl;
    lua_pop(L, lua_gettop(L));
    lua_pushboolean(L, false);
    lua_pushstring(L, em.c_str());
    return 2;
  }
}

static const luaL_Reg hotrodlib[] = {
  // {"start", &start},
  // {"stop", &stop},
  {"get", &get},
  {"put", &put},
  {NULL, NULL}
};

int luaopen_hotrodwrapper(lua_State *L) {
  start(L);
  luaL_register(L, "hotrodwrapper", hotrodlib);
  return 1;
}
