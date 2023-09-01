package com.example.springsecurityclient.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello my friends!";
    }

    @GetMapping("api/hello")
    public String helloApi() {
        return "API - Hello my friends!";
    }
}
