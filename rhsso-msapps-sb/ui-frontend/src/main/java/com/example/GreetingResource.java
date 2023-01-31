package com.example;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class GreetingResource {

    @Value("${apibackend.url}")
    private String apiBackend;

    private final RestTemplate restTemplate;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    public GreetingResource(RestTemplateBuilder builder) {
        RestTemplate rest = builder.build();

        // Retrieve the current access token and add it to the restTemplate
        rest.getInterceptors().add((request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
                OAuth2AuthorizedClient authorizedClient = this.authorizedClientService.loadAuthorizedClient(
                    auth.getAuthorizedClientRegistrationId(), auth.getName());
                OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                request.getHeaders().setBearerAuth(accessToken.getTokenValue());
            }
            return execution.execute(request, body);
        });

        this.restTemplate = rest;
    }

    private Map<String, String> callApiBackend(String path) {
        String resp = this.restTemplate.getForObject(this.apiBackend + path, String.class);
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", resp);
        map.put("server", "ui-frontend");
        return map;
    }

    @RequestMapping("/public")
    public Map<String, String> publicArea() {
        return callApiBackend("/public");
    }

    @RequestMapping("/protected")
    public Map<String, String> protectedArea() {
        return callApiBackend("/protected");
    }

    @RequestMapping("/protected/admin")
    public Map<String, String> protectedAreaWithAdminRole() {
        return callApiBackend("/protected/admin");
    }

    // For debug
    @RequestMapping("/whoami")
    public Object whoami() {
        return SecurityContextHolder.getContext().getAuthentication();
    }    
}
