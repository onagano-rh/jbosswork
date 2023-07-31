package com.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.*;
import org.infinispan.client.hotrod.configuration.*;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.client.hotrod.jmx.RemoteCacheClientStatisticsMXBean;
import org.infinispan.commons.configuration.ClassAllowList;
import org.infinispan.jboss.marshalling.commons.GenericJBossMarshaller;

public class HotRodClient {
    private static final Logger LOG = LogManager.getLogger(HotRodClient.class);

    private RemoteCacheManager cacheManager;
    private RemoteCache<String, Object> cache;
    private RemoteCacheClientStatisticsMXBean clientStatistics;

    public HotRodClient(String serverList, String cacheName) {
        List <String> list = new ArrayList<String>();
        list.add("com.nttdocomo.mobills.bis.common.cache.*");
        list.add("java.util.*");
        ClassAllowList allowList = new ClassAllowList(list);

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .connectionPool().maxActive(1).exhaustedAction(ExhaustedAction.WAIT)
            .marshaller(new GenericJBossMarshaller(allowList));

        builder.remoteCache(cacheName).nearCacheMode(NearCacheMode.INVALIDATED)
            .nearCacheUseBloomFilter(true)
            .nearCacheMaxEntries(30);

        cacheManager = new RemoteCacheManager(builder.build());
        cache = cacheManager.getCache(cacheName);
        clientStatistics = cache.clientStatistics();

      LOG.info("Connected.");
    }

    public void disconnect() {
        LOG.info("disconnect called.");
        if (cacheManager != null) {
            cacheManager.stop();
            cacheManager = null;
            cache = null;
            LOG.info("Disconnected");
        }
    }

    // public void setCache(String cacheName) {
    //     if (cacheName != null) {
    //         cache = cacheManager.getCache(cacheName);
    //     } else {
    //         cache = cacheManager.getCache();
    //     }
    //     LOG.info("Selected cache: " + cache.getName());
    // }

    public Object get(String key) {
        Object val = cache.get(key);
        LOG.info("NearCacheHits = {}", clientStatistics.getNearCacheHits());
        LOG.info("NearCacheMisses = {}", clientStatistics.getNearCacheMisses());
        LOG.info("NearCacheSize = {}", clientStatistics.getNearCacheSize());
        LOG.info("NearCacheInvalidations = {}", clientStatistics.getNearCacheInvalidations());
        return val;
    }

    public MetadataValue<Object> getWithMetadata(String key) {
        return cache.getWithMetadata(key);
    }

    public Object put(String key, Object value) {
        return cache.put(key, value);
    }

    public Object putIfAbsent(String key, Object value) {
        return cache.withFlags(Flag.FORCE_RETURN_VALUE).putIfAbsent(key, value);
    }

    public Object putWithExpiration(String key, Object value, int expiration) {
        return cache.put(key, value, expiration, TimeUnit.SECONDS);
    }

    public Object remove(String key) {
        Object val = cache.remove(key);
        return val;
    }

    public Object containsKey(String key) {
        return cache.containsKey(key);
    }

    public int size() {
        return cache.size();
    }

    public int keySetSize() {
        return cache.keySet().size();
    }

    public void clear() {
        cache.clear();
    }

    public void clearAsync() {
        try {
            cache.clearAsync().get();
        }
        catch (Exception e) {
            LOG.error("Error in clearAsync", e);
        }
    }
}
