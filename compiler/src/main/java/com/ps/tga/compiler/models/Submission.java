package com.ps.tga.compiler.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Schema
@Validated
@Value
public class Submission {
    @Schema(name = "userId", description = "The user id of the client", type = "UUID")
    @NotEmpty(message = "The userId should exists")
    String userId;
    @Schema(name = "problemId", description = "The problem id of submission", type = "UUID")
    @NotEmpty(message = "The problem id should exists")
    String problemId;
    @Schema(name = "sourceCode", description = "The source code", type = "String")
    @NotEmpty(message = "The source code should exists")
    String sourceCode;
    @Schema(name = "language", description = "The language in which the source code is written", type = "enum", allowableValues = {"JAVA", "PYTHON"})
    @NotNull(message = "The language should exists")
    Language language;
    @Schema(name = "testCases", description = "The different test cases for the given problem", type = "map")
    @NotNull(message = "Test cases should exists")
    Map<String, TestCase> testCases;
}
