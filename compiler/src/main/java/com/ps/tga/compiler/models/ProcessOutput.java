package com.ps.tga.compiler.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class ProcessOutput {

    private String stdOut;
    @Setter
    private String stdErr;
    private long executionDuration;
    private int status;
}
