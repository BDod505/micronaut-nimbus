package com.micronaut.nimbus.models.transformer;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class MemoryUsageResult {
    private final long gsonMemoryKb;
    private final long jacksonMemoryKb;

    public MemoryUsageResult(long gsonMemoryKb, long jacksonMemoryKb) {
        this.gsonMemoryKb = gsonMemoryKb;
        this.jacksonMemoryKb = jacksonMemoryKb;
    }

    public long getGsonMemoryKb() {
        return gsonMemoryKb;
    }

    public long getJacksonMemoryKb() {
        return jacksonMemoryKb;
    }

    @Override
    public String toString() {
        return "MemoryUsageResult{" +
                "gsonMemoryKb=" + gsonMemoryKb +
                ", jacksonMemoryKb=" + jacksonMemoryKb +
                '}';
    }
}
