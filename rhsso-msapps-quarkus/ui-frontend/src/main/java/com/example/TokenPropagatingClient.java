package com.example;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.oidc.token.propagation.AccessTokenRequestFilter;

@RegisterRestClient
@RegisterProvider(AccessTokenRequestFilter.class)
@Path("/")
public interface TokenPropagatingClient {

    @GET
    @Path("/public")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> resPublic();

    @GET
    @Path("/protected")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> resProtected();

    @GET
    @Path("/protected/admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> resProtectedAdmin();
}
