package com.example;

import java.util.logging.*;

import org.infinispan.client.hotrod.*;
import org.infinispan.client.hotrod.configuration.*;

import java.util.Map;

public class HotRodClient {
    private static Logger LOG = Logger.getLogger(HotRodClient.class.getName());

    private RemoteCacheManager cacheManager;
    private RemoteCache<String, Object> cache;
    
    private EventLogListener listener = new EventLogListener();

    public HotRodClient(String serverList, String cacheName) {
        connect(serverList);
        setCache(cacheName);
    }

    private void connect(String serverList) {
        LOG.info("connect called: " + serverList);
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServers(serverList);
        //builder.maxRetries(0);
        builder.connectionTimeout(1500).socketTimeout(3000);
        cacheManager = new RemoteCacheManager(builder.build());
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

    public void setCache(String cacheName) {
        if (cacheName != null) {
            cache = cacheManager.getCache(cacheName);
        } else {
            cache = cacheManager.getCache();
        }
        LOG.info("Selected cache: " + cache.getName());
    }

    public Object get(String key) {
        Object val = cache.get(key);
        return val;
    }

    public MetadataValue<Object> getWithMetadata(String key) {
        return cache.getWithMetadata(key);
    }

    public void getBulk() {
        Map<String, Object> map = cache.getBulk();
        for (String k : map.keySet()) {
            Object v = map.get(k);
            LOG.info("Got " + k + " : " + v);
        }
    }

    public Object put(String key, Object value) {
        return cache.put(key, value);
    }

    public Object putIfAbsent(String key, Object value) {
        return cache.withFlags(Flag.FORCE_RETURN_VALUE).putIfAbsent(key, value);
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
            LOG.log(Level.FINE, "Error in clearAsync", e);
        }
    }
    
    public void addListener() {
        cache.addClientListener(listener);
        LOG.info("Added " + listener);
    }

    public void removeListener() {
        cache.removeClientListener(listener);
        LOG.info("Removed " + listener);
    }
}
