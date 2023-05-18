package com.ps.tga.compiler.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record CompilerResponse(
        @Schema(name = "compilationDuration", description = "Total time taken to compile the problem") Long compilationDuration,
        @Schema(name = "message", description = "The compilation message") String message,
        @Schema(name = "status", description = "The status of execution") CompilerStatus status,
        @Schema(name = "testCaseResults", description = "The test case results") Map<String, TestCaseResult> testCaseResults) {
}
