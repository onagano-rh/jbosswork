package com.example.restcache;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.infinispan.Cache;
import org.infinispan.query.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.jboss.logging.Logger;

/**
 * Examples:
 *   curl "http://localhost:8080/jdgquery/rest/the-default-cache/put/key01/aaa"
 *   curl "http://localhost:8080/jdgquery/rest/the-default-cache/get/key01"
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

    /**
     * http://infinispan.org/tutorials/simple/query/
     * curl "http://localhost:8080/jdgquery/rest/the-default-cache/query"
     */
    @GET
    @Path("query")
    @Produces(MediaType.TEXT_PLAIN)
    public String query() {
        LOG.infof("To query");
        Cache<String, Person> cache = getCache();
        cache.put("person1", new Person("William", "Shakespeare"));
        cache.put("person2", new Person("William", "Wordsworth"));
        cache.put("person3", new Person("John", "Milton"));
        // Obtain a query factory for the cache
        QueryFactory queryFactory = Search.getQueryFactory(cache);
        // Construct a query
        Query query = queryFactory.from(Person.class).having("name").eq("William").toBuilder().build();
        // Execute the query
        List<Person> matches = query.list();
        // List the results
        matches.forEach(person -> LOG.infof("Match: %s", person));
        return "{query}\n";
    }
}
