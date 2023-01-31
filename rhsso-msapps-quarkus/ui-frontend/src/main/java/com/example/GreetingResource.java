package com.example;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/")
public class GreetingResource {

    @Inject
    @RestClient
    TokenPropagatingClient restClient;

    @GET
    @Path("/public")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> resPublic() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", restClient.resPublic().toString());
        map.put("server", "ui-frontend");
        return map;
    }

    @GET
    @Path("/protected")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> resProtected() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", restClient.resProtected().toString());
        map.put("server", "ui-frontend");
        return map;
    }

    @GET
    @Path("/protected/admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> resProtectedAdmin() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", restClient.resProtectedAdmin().toString());
        map.put("server", "ui-frontend");
        return map;
    }
}
