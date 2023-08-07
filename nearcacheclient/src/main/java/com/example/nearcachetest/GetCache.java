package com.example.nearcachetest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.ExhaustedAction;
import org.infinispan.client.hotrod.configuration.NearCacheMode;

public class GetCache {
    private static final Logger LOG = LogManager.getLogger(GetCache.class);

    private int id;

    private volatile boolean reproduced = false;

    private RemoteCache<String, String> cache;

    public GetCache(int id) {
        this.id = id;
        ConfigurationBuilder builder = CacheUtils.commonConfig();
        builder.connectionPool().maxActive(1).exhaustedAction(ExhaustedAction.WAIT);
        builder.remoteCache(CacheUtils.CACHE_NAME)
            .nearCacheMode(NearCacheMode.INVALIDATED)
            .nearCacheUseBloomFilter(true)
            .nearCacheMaxEntries(2);
        RemoteCacheManager cm = new RemoteCacheManager(builder.build());
        this.cache = cm.getCache(CacheUtils.CACHE_NAME);
    }

    public boolean isReproduced() {
        return this.reproduced;
    }

    public void setReproduced(boolean reproduced) {
        this.reproduced = reproduced;
    }

    public String get(String key) {
        return cache.get(key);
    }
}
