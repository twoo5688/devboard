package com.devboard.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Health", description = "Service health")
public class HealthCheckController {

	@GetMapping("/api/health")
	@Operation(summary = "Health check")
	public Map<String, String> getHealth() {
		return Map.of("status", "UP", "message", "DevBoard backend is running!");
	}

}
