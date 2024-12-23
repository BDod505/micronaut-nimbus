package com.micronaut.nimbus.service;

import com.micronaut.nimbus.engine.GsonJsonTransformer;
import com.micronaut.nimbus.engine.JsonTransformer;
import com.micronaut.nimbus.models.transformer.AnalysisResult;
import com.micronaut.nimbus.models.transformer.MemoryUsageResult;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class JsonTransformerService {

    private static final Logger LOG = LoggerFactory.getLogger(JsonTransformerService.class);

    private final JsonTransformer jsonTransformer;
    private final GsonJsonTransformer gsonTransformer;

    public JsonTransformerService(JsonTransformer jsonTransformer, GsonJsonTransformer gsonTransformer) {
        this.jsonTransformer = jsonTransformer;
        this.gsonTransformer = gsonTransformer;
    }

    public Object jsonTransformerTransform(Object input) throws IllegalAccessException {
        return jsonTransformer.transform(input);
    }

    public AnalysisResult performAnalysis(Object input) {
        LOG.info("Starting JSON performance analysis with payload of type: {}", input.getClass().getName());

        try {
            // Execution Time Benchmark
            AnalysisResult.ExecutionTimeResult gsonTimes = benchmarkExecutionTime(gsonTransformer, input, 100);
            AnalysisResult.ExecutionTimeResult jacksonTimes = benchmarkExecutionTime(jsonTransformer, input, 100);

            // Memory Usage Benchmark
            MemoryUsageResult memoryUsage = benchmarkMemoryUsage(gsonTransformer, jsonTransformer, input);

            // Concurrency Benchmark
            int threads = 10;
            int totalTasks = threads * 5; // Each thread runs 5 tasks
            long totalConcurrencyTime = benchmarkConcurrency(gsonTransformer, jsonTransformer, input, threads);

            LOG.info("JSON performance analysis completed successfully.");
            return new AnalysisResult(gsonTimes, jacksonTimes, memoryUsage, threads, totalTasks, totalConcurrencyTime);

        } catch (Exception e) {
            LOG.error("Error during JSON performance analysis: {}", e.getMessage(), e);
            throw new RuntimeException("JSON performance analysis failed. Please check the logs for details.", e);
        }
    }

    private AnalysisResult.ExecutionTimeResult benchmarkExecutionTime(Object transformer, Object payload, int iterations) throws Exception {
        List<Long> times = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            long time = measureExecutionTime(() -> {
                try {
                    if (transformer instanceof GsonJsonTransformer gson) {
                        gson.transform(payload);
                    } else if (transformer instanceof JsonTransformer jackson) {
                        jackson.transform(payload);
                    }
                } catch (Exception e) {
                    LOG.error("Transformation error: {}", e.getMessage(), e);
                }
            });
            times.add(time);
        }

        double avg = times.stream().mapToLong(Long::longValue).average().orElse(0);
        long min = times.stream().mapToLong(Long::longValue).min().orElse(0);
        long max = times.stream().mapToLong(Long::longValue).max().orElse(0);

        return new AnalysisResult.ExecutionTimeResult(min, max, avg);
    }

    private long measureExecutionTime(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return (System.nanoTime() - start) / 1_000_000; // Convert to milliseconds
    }

    private MemoryUsageResult benchmarkMemoryUsage(GsonJsonTransformer gsonTransformer, JsonTransformer jacksonTransformer, Object payload) throws Exception {
        Runtime runtime = Runtime.getRuntime();

        runtime.gc();
        long beforeGsonMemory = runtime.totalMemory() - runtime.freeMemory();
        gsonTransformer.transform(payload);
        long afterGsonMemory = runtime.totalMemory() - runtime.freeMemory();

        runtime.gc();
        long beforeJacksonMemory = runtime.totalMemory() - runtime.freeMemory();
        jacksonTransformer.transform(payload);
        long afterJacksonMemory = runtime.totalMemory() - runtime.freeMemory();

        return new MemoryUsageResult(
                (afterGsonMemory - beforeGsonMemory) / 1024, // Convert to KB
                (afterJacksonMemory - beforeJacksonMemory) / 1024 // Convert to KB
        );
    }

    private long benchmarkConcurrency(GsonJsonTransformer gsonTransformer, JsonTransformer jacksonTransformer, Object payload, int threads) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        long start = System.currentTimeMillis();

        try {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < threads * 5; i++) { // Each thread handles 5 tasks
                tasks.add(i % 2 == 0 ? createTask(gsonTransformer, payload) : createTask(jacksonTransformer, payload));
            }

            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            LOG.error("Concurrency benchmark interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        return System.currentTimeMillis() - start;
    }

    private Callable<Void> createTask(Object transformer, Object payload) {
        return () -> {
            try {
                if (transformer instanceof GsonJsonTransformer gson) {
                    gson.transform(payload);
                } else if (transformer instanceof JsonTransformer jackson) {
                    jackson.transform(payload);
                }
            } catch (Exception e) {
                LOG.error("Error in concurrency task: {}", e.getMessage(), e);
            }
            return null;
        };
    }
}