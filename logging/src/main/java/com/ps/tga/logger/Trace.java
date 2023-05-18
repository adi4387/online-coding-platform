package com.ps.tga.logger;

import java.util.Optional;
import java.util.UUID;

public record Trace(String traceId) {

    public static final String TRACE_ID_KEY = "traceId";
    public static Trace random() {
        return new Trace(randomUUID());
    }

    public static Trace prefixed(String prefix) {
        return new Trace(prefix + randomUUID());
    }

    private static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static Trace getTraceIfNotPresent(final String traceId) {
        return Optional.ofNullable(traceId)
                .map(Trace::new)
                .orElse(Trace.random());
    }
}