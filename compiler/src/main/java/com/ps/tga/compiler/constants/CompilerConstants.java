package com.ps.tga.compiler.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CompilerConstants {

    public static final String JAVA_INTERPRETER_IMAGE_NAME = "docker.io/adityakumarsingh88/jre";
    public static final String JAVA_INTERPRETER_CONTAINER_NAME = "java-interpreter";
    public static final String COMPILE_CODE_FILE_NAME = "COMPILE_CODE_FILE_NAME";
    public static final String TEST_CASE_FILE_NAME = "TEST_CASE_FILE_NAME";
    public static final String JAVA_COMPILER_IMAGE_NAME = "docker.io/adityakumarsingh88/jdk";
    public static final String JAVA_COMPILER_CONTAINER_NAME = "java-compiler";
    public static final String SOURCE_CODE_FILE_NAME = "SOURCE_CODE_FILE_NAME";
    public static final int DEFAULT_TIMEOUT = 60000;
    public static final String PYTHON_INTERPRETER_IMAGE_NAME = "docker.io/adityakumarsingh88/python";
    public static final String PYTHON_INTERPRETER_CONTAINER_NAME = "python-interpreter";
    public static final String EXECUTION_PATH = "EXECUTION_PATH";
    public static final String COMPILE_CODE = "codeCompiled";
    public static final String STATUS = "status";
    public static final String LANGUAGE = "language";
    public static final String EXECUTION_PATH_ON_HOST = "executionPathOnHost";
    public static final String EXECUTION_PATH_INSIDE_CONTAINER = "executionPathInsideContainer";
    public static final String EXECUTE_CODE = "codeExecuted";

}
