package com.ps.tga.compiler.services.compilation;

import com.ps.tga.compiler.services.CompilerProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.*;
import static java.nio.file.Files.writeString;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JavaCompilerTest {

    @Autowired
    protected JavaCompiler javaCompiler;

    @Autowired
    protected CompilerProperties compilerProperties;

    @Test
    void whenJavaCodeIsSubmittedTheJdkDockerContainerIsSpunUpAndCompilesTheCode() throws IOException {
        // given
        var sourceCode = readString(Path.of("src/test/resources/sourcecode/sum/sum.java"));
        String userId = "d7c31561-be46-4a5d-8154-01e5115b67ca";
        String problemId = "07eb5774-6fe2-4b3f-bf79-7b6d7456c0cc";
        var executionPathOnHost = compilerProperties.hostMountedPath() + "/" + userId + "/" + problemId + "/java_source_code";
        createDirectories(Path.of(executionPathOnHost));
        var sourceCodeFileName = "Main.java";
        writeString(Path.of(executionPathOnHost + "/" + sourceCodeFileName), sourceCode);
        var executionPathInsideContainer = compilerProperties.containerMountedPath() + "/" + userId + "/" + problemId + "/python_source_code";


        // when
        javaCompiler.compile(executionPathInsideContainer);

        assertTrue(exists(Path.of(executionPathOnHost + "/Main.class")));
    }
}
