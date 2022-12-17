package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class GreetingResource {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

    @GET
    @Path("/public")
    @Produces(MediaType.TEXT_PLAIN)
    public String resPublic() {
        return "public";
    }

    @GET
    @Path("/protected")
    @Produces(MediaType.TEXT_PLAIN)
    public String resProtected() {
        return "protected";
    }

    @GET
    @Path("/protected/admin")
    @Produces(MediaType.TEXT_PLAIN)
    public String resProtectedAdmin() {
        return "protected admin";
    }
}