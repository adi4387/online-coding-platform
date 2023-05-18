package com.ps.tga.logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TraceTest {

    @Test
    void should_create_prefixed_trace() {
        // given
        var prefix = "anyPrefix-";

        // when
        var trace = Trace.prefixed(prefix);

        // then
        assertTrue(trace.traceId().startsWith(prefix));
    }

}
