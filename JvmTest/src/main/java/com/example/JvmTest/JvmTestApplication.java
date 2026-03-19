package com.example.JvmTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@SpringBootApplication
public class JvmTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(JvmTestApplication.class, args);
    }

    // Health-ish endpoint
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    // Creates allocations + CPU (useful for GC/JFR demo)
    @GetMapping("/work")
    public Map<String, Object> work(@RequestParam(defaultValue = "200") int kb) {
        System.out.println("Received work request: " + kb + " KB");

        // allocate ~kb KB
        byte[] bytes = new byte[kb * 1024];
        ThreadLocalRandom.current().nextBytes(bytes);

        // some CPU work
        long sum = 0;
        for (int i = 0; i < 200_000; i++) sum += i;

        return Map.of("allocatedKB", kb, "sum", sum, "sample", bytes[0]);
    }
}
