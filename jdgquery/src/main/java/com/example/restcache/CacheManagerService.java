package com.example.restcache;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.Index;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.logging.Logger;

@Singleton
@Startup
public class CacheManagerService {
    private static Logger LOG = Logger.getLogger(CacheManagerService.class);

    private DefaultCacheManager instance = null;

    public DefaultCacheManager getCacheManager() {
        return instance;
    }

    @PostConstruct
    public void init() {
        if (instance == null) {
            try {
                instance = createCacheManager();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LOG.info("Initialized");
        }
    }

    @PreDestroy
    public void stop() {
        if (instance != null) {
            instance.stop();
            instance = null;
            LOG.info("Stopped");
        }
    }

    private DefaultCacheManager createCacheManagerWithXML() throws IOException {
        return new DefaultCacheManager("infinispan-config.xml");
    }

    private DefaultCacheManager createCacheManager() throws IOException {
        GlobalConfiguration gc = new GlobalConfigurationBuilder()
                .transport()
                    .defaultTransport()
                    .clusterName("jdgquery-cluster")
                    .addProperty("configurationFile", "jgroups-tcp.xml")
                .globalJmxStatistics()
                    .allowDuplicateDomains(true)
                    .enable()
                .build();
        Configuration lc = new ConfigurationBuilder()
                .clustering()
                    .cacheMode(CacheMode.DIST_ASYNC)
                .hash()
                    .numOwners(2)
                .jmxStatistics()
                    .enable()
                .indexing()
                    .index(Index.LOCAL)
                    .addProperty("lucene_version", "LUCENE_CURRENT")
                    .addProperty("default.directory_provider", "infinispan")
                    .addProperty("default.indexmanager", "org.infinispan.query.indexmanager.InfinispanIndexManager")
                    .addProperty("hibernate.search.infinispan.configuration_resourcename", "default-hibernatesearch-infinispan.xml")
                    .addIndexedEntity(Person.class)
                .build();
        return new DefaultCacheManager(gc, lc);
    }
}
