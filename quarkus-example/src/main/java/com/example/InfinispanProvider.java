package com.example;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

@ApplicationScoped
public class InfinispanProvider {

    private RemoteCacheManager cacheManager;
    private RemoteCache<String, String> cache;

    public InfinispanProvider() {
        this.cacheManager = new RemoteCacheManager();
        this.cache = cacheManager.getCache("default");
    }

    public String get(String key) {
        return cache.get(key);
    }

    public void put(String key, String value) {
        cache.put(key, value);
    }

    @PreDestroy
    public void destroy() {
        cacheManager.stop();
    }
}
