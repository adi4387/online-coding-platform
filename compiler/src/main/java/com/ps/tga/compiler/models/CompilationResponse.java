package com.ps.tga.compiler.models;

public record CompilationResponse(Long compilationDuration,
                                  String message,
                                  CompilerStatus status) {
}
