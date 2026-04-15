package com.devboard.backend.controller;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/api/health")
    public Map<String, String> getHealth(){
        return Map.of("status", "UP", "message", "DevBoard backend is running!");
    }
}
