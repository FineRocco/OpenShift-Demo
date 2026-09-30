package com.teejey.openshiftdemo.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST controller exposing demo and diagnostic endpoints for OpenShift.
 */
@RestController
public class HelloController {

    private final Environment environment;

    public HelloController(Environment environment) {
        this.environment = environment;
    }

    /**
     * Root endpoint providing application status and version.
     */
    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Welcome to OpenShift Demo!");
        response.put("status", "running");
        response.put("version", "1.0.0");
        return response;
    }

    /**
     * Hello endpoint returning a welcoming greeting and current ISO timestamp.
     */
    @GetMapping("/api/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("greeting", "Hello from OpenShift!");
        response.put("timestamp", Instant.now().toString());
        return response;
    }

    /**
     * Health endpoint indicating service status and active environment/profile.
     */
    @GetMapping("/api/health")
    public Map<String, Object> health() {
        String[] activeProfiles = environment.getActiveProfiles();
        String currentEnv = (activeProfiles != null && activeProfiles.length > 0)
                ? String.join(",", activeProfiles)
                : "default";

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("environment", currentEnv);
        return response;
    }

    /**
     * Info endpoint detailing application name, description, and runtime Java version.
     */
    @GetMapping("/api/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("app", "openshift-demo");
        response.put("description", "A Spring Boot app deployed on OpenShift");
        response.put("java", System.getProperty("java.version"));
        return response;
    }
}
