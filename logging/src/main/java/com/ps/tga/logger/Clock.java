package com.ps.tga.logger;

import java.time.Instant;
import java.util.function.Supplier;

class Clock {

    private static Supplier<Instant> factory = Instant::now;

    private Clock() {
    }

    static void fixed(Instant fixedTime) {
        factory = () -> fixedTime;
    }

    static String now() {
        return factory.get().toString();
    }

}
