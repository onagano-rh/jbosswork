package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MyApp {

    @Autowired
    InfinispanProvider ispn;

    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }

    @GetMapping("/{key}")
    public String get(@PathVariable("key") String key) {
        return ispn.get(key);
    }

    @PostMapping("/{key}")
    public void put(@PathVariable("key") String key, @RequestBody String value) {
        ispn.put(key, value);
    }
}