package com.example;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

@Component
public class InfinispanProvider implements DisposableBean {

    private final RemoteCacheManager cacheManager;
    private String cacheName = "default";
    private RemoteCache<String, String> cache;

    public InfinispanProvider() {
        this.cacheManager = new RemoteCacheManager();
        this.cache = cacheManager.getCache(cacheName);
    }

    public String get(String key) {
        return cache.get(key);
    }

    public void put(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public void destroy() throws Exception {
        cacheManager.stop();
    }
}