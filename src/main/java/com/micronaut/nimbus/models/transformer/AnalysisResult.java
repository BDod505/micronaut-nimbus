package com.micronaut.nimbus.models.transformer;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class AnalysisResult {
    private final ExecutionTimeResult gsonExecutionTime;
    private final ExecutionTimeResult jacksonExecutionTime;
    private final MemoryUsageResult memoryUsage;
    private final int concurrencyThreads;
    private final int totalTasks;
    private final long totalConcurrencyTimeMs;

    public AnalysisResult(ExecutionTimeResult gsonExecutionTime,
                          ExecutionTimeResult jacksonExecutionTime,
                          MemoryUsageResult memoryUsage,
                          int concurrencyThreads,
                          int totalTasks,
                          long totalConcurrencyTimeMs) {
        this.gsonExecutionTime = gsonExecutionTime;
        this.jacksonExecutionTime = jacksonExecutionTime;
        this.memoryUsage = memoryUsage;
        this.concurrencyThreads = concurrencyThreads;
        this.totalTasks = totalTasks;
        this.totalConcurrencyTimeMs = totalConcurrencyTimeMs;
    }

    public ExecutionTimeResult getGsonExecutionTime() {
        return gsonExecutionTime;
    }

    public ExecutionTimeResult getJacksonExecutionTime() {
        return jacksonExecutionTime;
    }

    public MemoryUsageResult getMemoryUsage() {
        return memoryUsage;
    }

    public int getConcurrencyThreads() {
        return concurrencyThreads;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public long getTotalConcurrencyTimeMs() {
        return totalConcurrencyTimeMs;
    }

    @Override
    public String toString() {
        return "AnalysisResult{" +
                "gsonExecutionTime=" + gsonExecutionTime +
                ", jacksonExecutionTime=" + jacksonExecutionTime +
                ", memoryUsage=" + memoryUsage +
                ", concurrencyThreads=" + concurrencyThreads +
                ", totalTasks=" + totalTasks +
                ", totalConcurrencyTimeMs=" + totalConcurrencyTimeMs +
                '}';
    }

    @Serdeable
    public static class ExecutionTimeResult {
        private final long minTimeMs;
        private final long maxTimeMs;
        private final double avgTimeMs;

        public ExecutionTimeResult(long minTimeMs, long maxTimeMs, double avgTimeMs) {
            this.minTimeMs = minTimeMs;
            this.maxTimeMs = maxTimeMs;
            this.avgTimeMs = avgTimeMs;
        }

        public long getMinTimeMs() {
            return minTimeMs;
        }

        public long getMaxTimeMs() {
            return maxTimeMs;
        }

        public double getAvgTimeMs() {
            return avgTimeMs;
        }

        @Override
        public String toString() {
            return "ExecutionTimeResult{" +
                    "minTimeMs=" + minTimeMs +
                    ", maxTimeMs=" + maxTimeMs +
                    ", avgTimeMs=" + avgTimeMs +
                    '}';
        }
    }
}
