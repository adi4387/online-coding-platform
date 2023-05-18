package com.ps.tga.compiler.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static com.ps.tga.compiler.constants.CompilerConstants.*;
import static com.ps.tga.compiler.utils.CmdUtils.compareOutput;
import static java.nio.file.Files.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
class ContainerServiceTest {

    @Autowired
    protected ContainerService containerService;

    @Autowired
    protected CompilerProperties compilerProperties;

    @Test
    void whenJavaCodeIsSubmittedTheJdkDockerContainerIsSpunUpAndCompilesTheCode() throws IOException {
        // given
        var sourceCode = readString(Path.of("src/test/resources/sourcecode/sum/sum.java"));
        var input1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase1_input.txt"));
        String userId = "d7c31561-be46-4a5d-8154-01e5115b67ca";
        String problemId = "07eb5774-6fe2-4b3f-bf79-7b6d7456c0cc";
        var compileCodeFileName = "Main";
        var executionPathOnHost = compilerProperties.hostMountedPath() + "/" + userId + "/" + problemId + "/java_source_code";
        var executionPathInsideContainer = compilerProperties.containerMountedPath() + "/" + userId + "/" + problemId + "/java_source_code";

        createDirectories(Path.of(executionPathOnHost));
        var sourceCodeFileName = "Main.java";
        var testCaseFileName = "testCase1_input.txt";
        writeString(Path.of(executionPathOnHost + "/" + sourceCodeFileName), sourceCode);
        writeString(Path.of(executionPathOnHost + "/" + testCaseFileName), input1);

        // when
        var processOutput = containerService.runContainer(JAVA_COMPILER_IMAGE_NAME,
                JAVA_COMPILER_CONTAINER_NAME,
                DEFAULT_TIMEOUT,
                Map.of(SOURCE_CODE_FILE_NAME, sourceCodeFileName,
                        EXECUTION_PATH, executionPathInsideContainer));

        // then
        assertTrue(exists(Path.of(executionPathOnHost + "/" + "Main.class")));

        // when
        processOutput = containerService.runContainer(JAVA_INTERPRETER_IMAGE_NAME,
                JAVA_INTERPRETER_CONTAINER_NAME,
                DEFAULT_TIMEOUT,
                Map.of(
                        COMPILE_CODE_FILE_NAME, compileCodeFileName,
                        TEST_CASE_FILE_NAME,"testCase1_input.txt",
                        EXECUTION_PATH, executionPathInsideContainer
                )
        );

        assertTrue(compareOutput("3", processOutput.stdOut()));
    }

    @Test
    void whenPythonCodeIsSubmittedThePythonDockerContainerIsSpunUpAndCompilesTheCode() throws IOException {
        // given
        var sourceCode = readString(Path.of("src/test/resources/sourcecode/sum/sum.py"));
        var input1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase1_input.txt"));
        String userId = "d7c31561-be46-4a5d-8154-01e5115b67ca";
        String problemId = "07eb5774-6fe2-4b3f-bf79-7b6d7456c0cc";
        var executionPathOnHost = compilerProperties.hostMountedPath() + "/" + userId + "/" + problemId + "/python_source_code";
        var executionPathInsideContainer = compilerProperties.containerMountedPath() + "/" + userId + "/" + problemId + "/python_source_code";

        createDirectories(Path.of(executionPathOnHost));
        var sourceCodeFileName = "Main.py";
        var testCaseFileName = "testCase1_input.txt";
        writeString(Path.of(executionPathOnHost + "/" + sourceCodeFileName), sourceCode);
        writeString(Path.of(executionPathOnHost + "/" + testCaseFileName), input1);

        // when
        var processOutput = containerService.runContainer(PYTHON_INTERPRETER_IMAGE_NAME,
                PYTHON_INTERPRETER_CONTAINER_NAME,
                DEFAULT_TIMEOUT,
                Map.of(
                        SOURCE_CODE_FILE_NAME, sourceCodeFileName,
                        TEST_CASE_FILE_NAME,"testCase1_input.txt",
                        EXECUTION_PATH, executionPathInsideContainer
                )
        );

        assertTrue(compareOutput("3", processOutput.stdOut()));
    }
}
