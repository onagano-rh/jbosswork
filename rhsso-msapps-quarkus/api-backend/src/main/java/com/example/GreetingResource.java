package com.example;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class GreetingResource {

    @GET
    @Path("/public")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> resPublic() {
        final Map<String, String> res = new HashMap<String, String>();
        res.put("message", "public");
        res.put("server", "api-backend");
        return res;
    }

    @GET
    @Path("/protected")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> resProtected() {
        final Map<String, String> res = new HashMap<String, String>();
        res.put("message", "protected");
        res.put("server", "api-backend");
        return res;
    }

    @GET
    @Path("/protected/admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> resProtectedAdmin() {
        final Map<String, String> res = new HashMap<String, String>();
        res.put("message", "protected admin");
        res.put("server", "api-backend");
        return res;
    }
}
