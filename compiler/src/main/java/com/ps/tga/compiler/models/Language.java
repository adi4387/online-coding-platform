package com.ps.tga.compiler.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.ps.tga.compiler.constants.Folders.*;

@Schema
@Getter
@AllArgsConstructor
public enum Language {

    JAVA(true, JAVA_SOURCE_CODE_FOLDER, "Main.java", "Main"),
    PYTHON(true, PYTHON_SOURCE_CODE_FOLDER, "Main.py", "Main.py");

    private final boolean compilationRequired;
    private final String sourceCodeFolder;
    private final String sourceCodeFileName;
    private final String interpretedCodeFileName;
}
