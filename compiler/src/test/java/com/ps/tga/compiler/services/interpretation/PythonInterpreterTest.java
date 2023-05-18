package com.ps.tga.compiler.services.interpretation;

import com.ps.tga.compiler.services.CompilerProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;

import static com.ps.tga.compiler.utils.CmdUtils.compareOutput;
import static java.nio.file.Files.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PythonInterpreterTest {

    @Autowired
    protected PythonInterpreter pythonInterpreter;

    @Autowired
    protected CompilerProperties compilerProperties;

    @Test
    void whenJavaCodeIsSuccessfullyCompiledThenJREDockerContainerIsSpunUpAndRunsTheCode() throws IOException {
        // given
        var sourceCode = readString(Path.of("src/test/resources/sourcecode/sum/sum.py"));
        var input1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase1_input.txt"));
        String userId = "d7c31561-be46-4a5d-8154-01e5115b67ca";
        String problemId = "07eb5774-6fe2-4b3f-bf79-7b6d7456c0cc";
        var executionPath = compilerProperties.hostMountedPath() + "/" + userId + "/" + problemId + "/python_source_code";
        createDirectories(Path.of(executionPath));
        var sourceCodeFileName = "Main.py";
        var testCaseFileName = "testCase1_input.txt";
        writeString(Path.of(executionPath + "/" + sourceCodeFileName), sourceCode);
        writeString(Path.of(executionPath + "/" + testCaseFileName), input1);
        var executionPathInsideContainer = compilerProperties.containerMountedPath() + "/" + userId + "/" + problemId + "/python_source_code";

        // when
        var interpreterResponse = pythonInterpreter.interpret(executionPathInsideContainer, testCaseFileName);

        assertTrue(compareOutput("3", interpreterResponse.output()));
    }
}
