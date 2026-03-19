package com.example.jvmtuning.controller;

import com.example.jvmtuning.service.WorkloadService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
public class WorkloadController {

    private final WorkloadService workloadService;

    public WorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return Map.of(
                "message", "JVM tuning demo is running",
                "cpuEndpoint", "/api/cpu/primes?limit=20000",
                "sortEndpoint", "/api/cpu/sort?items=1500",
                "allocateEndpoint", "/api/memory/allocate?sizeMb=8&iterations=20",
                "retainEndpoint", "/api/memory/retain?sizeMb=10",
                "prometheusEndpoint", "/actuator/prometheus"
        );
    }

    @GetMapping("/cpu/primes")
    public WorkloadService.PrimeResponse calculatePrimes(@RequestParam(defaultValue = "20000") @Min(1000) @Max(200000) int limit) {
        return workloadService.calculatePrimes(limit);
    }

    @GetMapping("/cpu/sort")
    public WorkloadService.SortResponse sortRandomStrings(@RequestParam(defaultValue = "1500") @Min(100) @Max(10000) int items) {
        return workloadService.sortRandomStrings(items);
    }

    @GetMapping("/memory/allocate")
    public WorkloadService.AllocationResponse allocateMemory(
            @RequestParam(defaultValue = "8") @Min(1) @Max(64) int sizeMb,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) int iterations) {
        return workloadService.allocateTemporaryMemory(sizeMb, iterations);
    }

    @GetMapping("/memory/retain")
    public WorkloadService.RetainResponse retainMemory(@RequestParam(defaultValue = "10") @Min(1) @Max(64) int sizeMb) {
        return workloadService.retainMemory(sizeMb);
    }

    @DeleteMapping("/memory/retain")
    public WorkloadService.RetainResponse clearRetainedMemory() {
        return workloadService.clearRetainedMemory();
    }

    @GetMapping("/memory/state")
    public WorkloadService.MemoryState currentMemoryState() {
        return workloadService.currentMemoryState();
    }
}
