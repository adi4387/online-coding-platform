package com.ps.tga.logger;

import java.io.PrintWriter;
import java.io.StringWriter;

class ExceptionData {
    private static final int MAX_STACKTRACE_SIZE = 6_000;

    String type;
    String cause;
    String stacktrace;

    ExceptionData(Throwable ex) {
        type = ex.getClass().getSimpleName();
        cause = ex.getMessage();
        stacktrace = customStackTrace(ex);
    }

    static String customStackTrace(Throwable ex) {
        var stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        var stackTrace = stringWriter.toString();
        return stackTrace.substring(0, Math.min(stackTrace.length(), MAX_STACKTRACE_SIZE));
    }

}
