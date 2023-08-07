package com.example.nearcachetest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgroups.blocks.Cache;

/**
 * Edit CacheUtils.commnConfig() for your connection parameters.
 * Usage:
 *   mvn clean compile exec:java -Dexec.mainClass=com.example.nearcachetest.Main
 */
public class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        PutCache putCache = new PutCache();

        int numOfGetCaches = CacheUtils.GETTER_CONCURRENCY;
        List<GetCache> getters = new LinkedList<GetCache>();
        for (int i = 0; i < numOfGetCaches; i++) getters.add(new GetCache(i));
        ExecutorService executorService = Executors.newFixedThreadPool(numOfGetCaches);

        int count = 0;
        try {
            loop: while (true) {
                String key = CacheUtils.getRandomKey();
                String value = String.format("VALUE-%d", System.currentTimeMillis());
                putCache.put(key, value);
                Thread.sleep(1000L); // give a chance to update near caches.
                LOG.info("Loop count: {}", ++count);

                CountDownLatch latch = new CountDownLatch(numOfGetCaches);
                for (GetCache g: getters) {
                    executorService.submit(() -> {
                        String nearCacheValue = g.get(key);
                        if (!value.equals(nearCacheValue)) g.setReproduced(true);
                        latch.countDown();
                    });
                }
                latch.await();
                for (GetCache g: getters) if (g.isReproduced()) break loop;
            }
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            executorService.shutdown();
        }
    }
    
}
