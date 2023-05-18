package com.ps.tga.compiler.models;

public record InterpreterResponse(Long compilationDuration,
                                  String output,
                                  CompilerStatus status) {
}
