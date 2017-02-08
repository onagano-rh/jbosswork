package com.example.standalonecache;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class App
{
    static final Logger LOG = LogManager.getLogger(App.class);

    int maxEntries = Integer.parseInt(System.getProperty("maxEntries", "10"));
    int numberOfPut = Integer.parseInt(System.getProperty("numberOfPut", "100"));

    void runTest() {
        LOG.entry();
        LOG.info("Start now");
//        Configuration c = new ConfigurationBuilder()
//            .clustering().cacheMode(CacheMode.LOCAL)
//            .eviction().maxEntries(maxEntries)
//            .build();
        EmbeddedCacheManager cm = null;
        try {
            cm = new DefaultCacheManager("sample.xml");
        }
        catch (IOException ioe) {
            throw new RuntimeException("No config file", ioe);
        }
        Cache<String, String> cache = cm.getCache();

        for (int i = 1; i <= numberOfPut; i++) {
            String key = "KEY_" + i;
            String value = "VALUE_" + i;
            cache.put(key, value);

            if (i % maxEntries == 0) {
                System.out.printf("Loop: %d, cache size: %d\n", i, cache.size());
            }
        }
        LOG.exit();
    }

    public static void main(String[] args) {
        new App().runTest();
    }
}
