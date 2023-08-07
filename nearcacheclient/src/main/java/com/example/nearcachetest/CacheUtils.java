package com.example.nearcachetest;

import java.util.Random;

import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.jboss.marshalling.commons.GenericJBossMarshaller;

public class CacheUtils {

    public static final String CACHE_NAME = "jbmCache";

    public static final int GETTER_CONCURRENCY = 60;

    public static final int NUMBER_OF_KEYS = 2;

    private static final Random random = new Random(1L);
    
    public static String getRandomKey() {
        int i = random.nextInt(NUMBER_OF_KEYS);
        return String.format("KEY-%8d", i);
    }

    public static ConfigurationBuilder commonConfig() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder
            //.security().authentication().username("admin").password("password")
            .marshaller(new GenericJBossMarshaller())
            .addServers("127.0.0.1:11222");
        return builder;
    }
}
