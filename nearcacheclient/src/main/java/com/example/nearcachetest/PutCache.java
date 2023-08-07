package com.example.nearcachetest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

public class PutCache {
    private static final Logger LOG = LogManager.getLogger(PutCache.class);

    private RemoteCache<String, String> cache;

    public PutCache() {
        RemoteCacheManager cm = new RemoteCacheManager(CacheUtils.commonConfig().build());
        this.cache = cm.getCache(CacheUtils.CACHE_NAME);
    }

    public void put(String key, String value) {
        cache.put(key, value);
    }
}
