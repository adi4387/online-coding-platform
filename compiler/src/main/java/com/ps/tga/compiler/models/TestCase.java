package com.ps.tga.compiler.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TestCase(
        @Schema(name = "input", description = "The input for given problem", nullable = true) String input,
        @Schema(name = "expectedOutput",
                description = "the expected output for given problem") @NotNull(message = "The expected output should exists") String expectedOutput) {
}
