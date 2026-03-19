package com.example.jvmtuning.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class WorkloadService {

    private final List<byte[]> retainedMemory = new CopyOnWriteArrayList<>();
    private final SecureRandom secureRandom = new SecureRandom();
    private final Counter retainedChunksCounter;

    public WorkloadService(MeterRegistry meterRegistry) {
        this.retainedChunksCounter = Counter.builder("demo.retained.allocations.total")
                .description("Number of retained byte[] chunks created by the demo endpoint")
                .register(meterRegistry);

        Gauge.builder("demo.retained.memory.bytes", retainedMemory,
                        list -> list.stream().mapToLong(chunk -> chunk.length).sum())
                .description("Approximate memory retained by the demo leak endpoint")
                .baseUnit("bytes")
                .register(meterRegistry);

        Gauge.builder("demo.retained.memory.chunks", retainedMemory, List::size)
                .description("How many retained chunks are currently kept in memory")
                .register(meterRegistry);
    }

    @Timed(value = "demo.cpu.prime.seconds", description = "Time spent in prime calculation")
    public PrimeResponse calculatePrimes(int limit) {
        long start = System.nanoTime();
        List<Integer> primes = new ArrayList<>();
        for (int number = 2; number <= limit; number++) {
            if (isPrime(number)) {
                primes.add(number);
            }
        }
        long elapsedMs = Duration.ofNanos(System.nanoTime() - start).toMillis();
        return new PrimeResponse(limit, primes.size(), primes.isEmpty() ? -1 : primes.get(primes.size() - 1), elapsedMs);
    }

    @Timed(value = "demo.cpu.sort.seconds", description = "Time spent sorting random strings")
    public SortResponse sortRandomStrings(int items) {
        long start = System.nanoTime();
        List<String> values = IntStream.range(0, items)
                .mapToObj(i -> randomString(64))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.sort(values);
        long elapsedMs = Duration.ofNanos(System.nanoTime() - start).toMillis();
        return new SortResponse(items, values.get(0), values.get(values.size() - 1), elapsedMs);
    }

    @Timed(value = "demo.memory.allocate.seconds", description = "Time spent allocating temporary memory")
    public AllocationResponse allocateTemporaryMemory(int sizeMb, int iterations) {
        long start = System.nanoTime();
        long checksum = 0;
        for (int i = 0; i < iterations; i++) {
            byte[] block = new byte[sizeMb * 1024 * 1024];
            secureRandom.nextBytes(block);
            checksum += block[0] & 0xFF;
        }
        long elapsedMs = Duration.ofNanos(System.nanoTime() - start).toMillis();
        return new AllocationResponse(sizeMb, iterations, checksum, elapsedMs);
    }

    @Timed(value = "demo.memory.retain.seconds", description = "Time spent retaining memory")
    public RetainResponse retainMemory(int sizeMb) {
        byte[] block = new byte[sizeMb * 1024 * 1024];
        secureRandom.nextBytes(block);
        retainedMemory.add(block);
        retainedChunksCounter.increment();
        return new RetainResponse(sizeMb, retainedMemory.size(), retainedMemory.stream().mapToLong(chunk -> chunk.length).sum());
    }

    public RetainResponse clearRetainedMemory() {
        retainedMemory.clear();
        return new RetainResponse(0, 0, 0);
    }

    public MemoryState currentMemoryState() {
        return new MemoryState(retainedMemory.size(), retainedMemory.stream().mapToLong(chunk -> chunk.length).sum());
    }

    private boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }
        for (int i = 2; i * i <= number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    private String randomString(int byteCount) {
        byte[] bytes = new byte[byteCount];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes).toLowerCase(Locale.ROOT);
    }

    public record PrimeResponse(int limit, int primeCount, int largestPrime, long elapsedMs) {}
    public record SortResponse(int items, String first, String last, long elapsedMs) {}
    public record AllocationResponse(int sizeMb, int iterations, long checksum, long elapsedMs) {}
    public record RetainResponse(int sizeMb, int retainedChunks, long retainedBytes) {}
    public record MemoryState(int retainedChunks, long retainedBytes) {}
}
