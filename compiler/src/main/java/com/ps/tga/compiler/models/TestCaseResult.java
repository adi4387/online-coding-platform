package com.ps.tga.compiler.models;

import io.swagger.v3.oas.annotations.media.Schema;

public record TestCaseResult(
        @Schema(name = "input", description = "The input for given problem", nullable = true) String input,
        @Schema(name = "expectedOutput", description = "the expected output for given problem") String expectedOutput,
        @Schema(name = "output", description = "the output for given problem") String output,
        @Schema(name = "status", description = "The status of execution") CompilerStatus status,
        @Schema(name = "executionDuration", description = "Total time to execute the problem") Long executionDuration) {
}
