package com.ps.tga.logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.ps.tga.logger.JsonFactory.asString;

public class LogEvent {
    private static final String EVENT_TYPE = "eventType";
    private final Map<String, Object> message;

    private LogEvent() {
        message = new LinkedHashMap<>();
    }

    public static LogEvent eventType(String eventType) {
        return new LogEvent()
                .with(EVENT_TYPE, eventType);
    }

    public static LogEvent unexpectedError(Throwable ex) {
        return eventType("UnexpectedError").with("exception", new ExceptionData(ex));
    }

    public LogEvent with(String key, Object value) {
        message.put(key, value);
        return this;
    }

    public String asJSON() {
        message.put("timestamp", Clock.now());
        message.put("thread", Thread.currentThread().getName());
        return asString(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogEvent logEvent)) return false;

        return Objects.equals(message, logEvent.message);
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }
}
