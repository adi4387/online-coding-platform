package com.ps.tga.logger;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.ps.tga.logger.LogEvent.eventType;
import static com.ps.tga.logger.LogEvent.unexpectedError;
import static java.time.Instant.now;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class LogEventTest {

    @Test
    void should_create_log_event_with_system_data_timestamp_and_thread_name() throws JSONException {
        // given
        Clock.fixed(now());

        // when
        var logEvent = eventType("ApiCall")
                .with(
                        "request", Map.of(
                                "endpoint", "/v1/pipelines"
                        )
                )
                .with("TraceId", new Trace("trace-id"))
                .asJSON();

        // then
        var expectedLogEvent = "{\"eventType\":\"ApiCall\",\"traceId\":\"trace-id\",\"request\":{\"endpoint\":\"/v1/pipelines\"},\"timestamp\":\"" + Clock.now() + "\",\"thread\":\"" + Thread.currentThread().getName() + "\"}";
        assertEquals(expectedLogEvent, logEvent, true);
    }

    @Test
    void should_extend_log_message_with_exception_details() throws JSONException {
        // given
        Clock.fixed(now());
        var ex = new NullPointerException("NullPointerException description");

        // when
        var logEvent = unexpectedError(ex).asJSON();

        var exceptionDate = new ExceptionData(ex);

        // then
        var expectedLogEvent = "{\"eventType\":\"UnexpectedError\",\"exception\":{\"type\":\"NullPointerException\",\"cause\":\"NullPointerException description\",\"stacktrace\":\"" + exceptionDate.stacktrace + "\"},\"timestamp\":\"" + Clock.now() + "\",\"thread\":\"" + Thread.currentThread().getName() + "\"}";
        assertEquals(expectedLogEvent, logEvent, true);
    }
}
