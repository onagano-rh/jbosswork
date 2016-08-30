package com.example.restcache;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.infinispan.Cache;
import org.jboss.logging.Logger;

/**
 * Usage:
 *   curl "http://localhost:8080/restcache/rest/cachename/put/key01/aaa"
 *   curl "http://localhost:8080/restcache/rest/cachename/get/key01"
 */

@SuppressWarnings({"rawtypes", "unchecked"})
@Path("{cache}")
public class CacheOperations {
	private static Logger LOG = Logger.getLogger(CacheOperations.class);
	
    @Inject
    private CacheManagerService cmService;

    @PathParam("cache")
    private String cacheName;

	private Cache getCache() {
        return cmService.getCacheManager().getCache(cacheName);
    }

    @GET
    @Path("get/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getFromCache(@PathParam("key") String key) {
    	LOG.infof("To get key %s", key);
        Object value = getCache().get(key);
        return String.format("{key = %s, value = %s}\n", key, value);
    }

    @GET
    @Path("put/{key}/{value}")
    @Produces(MediaType.TEXT_PLAIN)
    public String putToCache(@PathParam("key") String key,
                             @PathParam("value") String value) {
    	LOG.infof("To put key %s, value %s", key, value);
        getCache().put(key, value);
        return String.format("{key = %s, value = %s}\n", key, value);
    }

    @GET
    @Path("remove/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public String removeFromCache(@PathParam("key") String key) {
    	LOG.infof("To remove key %s", key);
        getCache().remove(key);
        return "{removed}\n";
    }

    @GET
    @Path("clear")
    @Produces(MediaType.TEXT_PLAIN)
    public String clearCache() {
    	LOG.info("To clear cache");
        getCache().clear();
        return "{cleared}\n";
    }
    
    @GET
    @Path("filldata/{from}/{to}")
    @Produces(MediaType.TEXT_PLAIN)
    public String fillData(@PathParam("from") int from, @PathParam("to") int to) {
    	LOG.infof("To fill data from %d to %d", from, to);
    	if (from > to) return "{invalid from and to}\n";
    	for (int i = from; i <= to; i++) {
            getCache().put(String.format("key%06d", i), String.format("value%08d", i));
    	}
        return "{filled}\n";
    }
}
