package com.example.springbootkeycloak;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRestController {

    @RequestMapping("/public")
    public Map<String, String> publicArea() {
        Map map = new HashMap();
        map.put("message", "public area");
        return map;
    }

    @RequestMapping("/protected")
    public Map<String, String> protectedArea() {
        Map map = new HashMap();
        map.put("message", "protected area");
        return map;
    }

    @RequestMapping("/protected/admin")
    public Map<String, String> protectedAreaWithAdminRole() {
        Map map = new HashMap();
        map.put("message", "protected area with admin role");
        return map;
    }
}
