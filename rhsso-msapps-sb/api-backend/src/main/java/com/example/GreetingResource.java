package com.example;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingResource {

    @RequestMapping("/public")
    public Map<String, String> publicArea() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "public area");
        map.put("server", "api-backend");
        return map;
    }

    @RequestMapping("/protected")
    public Map<String, String> protectedArea() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "protected area");
        map.put("server", "api-backend");
        return map;
    }

    @RequestMapping("/protected/admin")
    public Map<String, String> protectedAreaWithAdminRole() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "protected area with admin role");
        map.put("server", "api-backend");
        return map;
    }

    // For debug
    @RequestMapping("/whoami")
    public Object whoami() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
