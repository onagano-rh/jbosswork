package com.example.restcache;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.infinispan.manager.DefaultCacheManager;
import org.jboss.logging.Logger;

@ApplicationScoped
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
				instance = new DefaultCacheManager("infinispan-config.xml");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
            instance.startCache("DUMMY");
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
}
