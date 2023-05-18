package com.ps.tga.compiler.services;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Value
@ConfigurationProperties(prefix = "env.execution")
public class CompilerProperties {

    String pathSeparator;
    String inputFileName;
    String expectedOutputFileName;
    String inputOutputFileExtension;
    boolean deleteSourceCode;
    String hostMountedPath;
    String containerMountedPath;
    String volume;
}
