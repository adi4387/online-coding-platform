package com.ps.tga.compiler.services;

import com.ps.tga.compiler.models.Submission;
import com.ps.tga.compiler.models.TestCase;
import com.ps.tga.compiler.utils.CmdUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static com.ps.tga.compiler.constants.Folders.JAVA_SOURCE_CODE_FOLDER;
import static com.ps.tga.compiler.models.CompilerStatus.FAILURE;
import static com.ps.tga.compiler.models.CompilerStatus.SUCCESS;
import static com.ps.tga.compiler.models.Language.JAVA;
import static com.ps.tga.compiler.models.Language.PYTHON;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.readString;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DirtiesContext
@SpringBootTest
class CompilerServiceTest {

    @Autowired
    protected CompilerService compilerService;

    @Autowired
    protected CompilerProperties compilerProperties;

    @Test
    void whenTheUserDoesSubmissionWithSourceCodeAndItsInputAndOutputTheyAreSavedInUniqueFolder() throws IOException {
        // given
        compilerProperties = new CompilerProperties(
                "/",
                "input",
                "output",
                ".txt",
                false,
                "/Users/adisingh16/learning/app",
                "/app",
                "/Users/adisingh16/learning/app"
        );
        compilerService = new CompilerService(compilerProperties,
                newCachedThreadPool());

        var sourceCode = readString(Path.of("src/test/resources/sourcecode/sum/sum.java"));
        var input1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase1_input.txt"));
        var output1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase_output.txt"));
        var input2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_input.txt"));
        var output2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_output.txt"));
        var userId = randomUUID().toString();
        var  problemId = randomUUID().toString();

        // when
        compilerService.compileAndExecute(
                    new Submission(userId, problemId,
                            sourceCode,
                            JAVA,
                            Map.of("testCase1", new TestCase(input1, output1),
                                    "testCase2", new TestCase(input2, output2)
                            ))
        );

        // then
        var sourceCodePath = compilerProperties.hostMountedPath()
                + compilerProperties.pathSeparator()
                + userId
                + compilerProperties.pathSeparator()
                + problemId
                + compilerProperties.pathSeparator()
                + JAVA_SOURCE_CODE_FOLDER;
        var expectedSourceCode = readString(Path.of(sourceCodePath + "/Main.java"));
        var expectedInput1 = readString(Path.of(sourceCodePath + "/testCase1_input.txt"));
        var expectedOutput1 = readString(Path.of(sourceCodePath + "/testCase1_output.txt"));
        var expectedInput2 = readString(Path.of(sourceCodePath + "/testCase2_input.txt"));
        var expectedOutput2 = readString(Path.of(sourceCodePath + "/testCase2_output.txt"));
        assertEquals(expectedSourceCode, sourceCode);
        assertEquals(expectedInput1, input1);
        assertEquals(expectedOutput1, output1);
        assertEquals(expectedInput2, input2);
        assertEquals(expectedOutput2, output2);
    }

    @Test
    void whenTheSourceCodeIsCompiledAndExecutedThenDeleteTheFolder() throws IOException {
        // given
        compilerProperties = new CompilerProperties(
                "/",
                "input",
                "output",
                ".txt",
                true,
                "/Users/adisingh16/learning/app",
                "/app",
                "/Users/adisingh16/learning/app"
        );
        compilerService = new CompilerService(compilerProperties,
                newCachedThreadPool());

        var sourceCode = readString(Path.of("src/test/resources/sourcecode/sum/sum.java"));
        var input1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase1_input.txt"));
        var output1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase_output.txt"));
        var input2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_input.txt"));
        var output2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_output.txt"));
        var userId = randomUUID().toString();
        var  problemId = randomUUID().toString();
        var path = Path.of(compilerProperties.hostMountedPath()
                + compilerProperties.pathSeparator()
                + userId);

        // when

        final var submission = new Submission(userId,
                problemId,
                sourceCode,
                JAVA,
                Map.of("testCase1", new TestCase(input1, output1),
                        "testCase2", new TestCase(input2, output2)
                ));
        compilerService.compileAndExecute(submission);
        await().until(() -> !exists(path));

        // then
        assertFalse(exists(path));
    }

    @Test
    void whenUserSubmitsTheJavaSourceCodeItIsCompiledAndExecutedWithAllTestPassing() throws IOException {
        // given
        compilerProperties = new CompilerProperties(
                "/",
                "input",
                "output",
                ".txt",
                true,
                "/Users/adisingh16/learning/app",
                "/app",
                "/Users/adisingh16/learning/app"
        );
        compilerService = new CompilerService(compilerProperties,
                newCachedThreadPool());

        var sourceCode = readString(Path.of("src/test/resources/sourcecode/sum/sum.java"));
        var input1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase1_input.txt"));
        var output1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase_output.txt"));
        var input2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_input.txt"));
        var output2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_output.txt"));
        var userId = randomUUID().toString();
        var  problemId = randomUUID().toString();

        // when
        String testCase1 = "testCase1";
        String testCase2 = "testCase2";
        final var submission = new Submission(userId,
                problemId,
                sourceCode,
                JAVA,
                Map.of(testCase1, new TestCase(input1, output1),
                        testCase2, new TestCase(input2, output2)
                ));
        var compilerResponse = compilerService.compileAndExecute(submission);

        // then
        assertEquals(SUCCESS, compilerResponse.status());
        assertTrue(compilerResponse.compilationDuration() > 0L);
        assertTrue(CmdUtils.compareOutput("3", compilerResponse.testCaseResults().get(testCase1).output()));
        assertTrue(CmdUtils.compareOutput("7", compilerResponse.testCaseResults().get(testCase2).output()));
    }

    @Test
    void whenUserSubmitsPythonSourceCodeItIsExecutedWithAllTestPassing() throws IOException {
        // given
        compilerProperties = new CompilerProperties(
                "/",
                "input",
                "output",
                ".txt",
                true,
                "/Users/adisingh16/learning/app",
                "/app",
                "/Users/adisingh16/learning/app"
        );
        compilerService = new CompilerService(compilerProperties,
                newCachedThreadPool());

        var sourceCode = readString(Path.of("src/test/resources/sourcecode/sum/sum.py"));
        var input1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase1_input.txt"));
        var output1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase_output.txt"));
        var input2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_input.txt"));
        var output2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_output.txt"));
        var userId = randomUUID().toString();
        var  problemId = randomUUID().toString();

        // when
        String testCase1 = "testCase1";
        String testCase2 = "testCase2";
        final var submission = new Submission(userId,
                problemId,
                sourceCode,
                PYTHON,
                Map.of(testCase1, new TestCase(input1, output1),
                        testCase2, new TestCase(input2, output2)
                ));
        var compilerResponse = compilerService.compileAndExecute(submission);

        // then
        assertEquals(SUCCESS, compilerResponse.status());
        assertEquals(0L, compilerResponse.compilationDuration());
        assertTrue(CmdUtils.compareOutput("3", compilerResponse.testCaseResults().get(testCase1).output()));
        assertTrue(CmdUtils.compareOutput("7", compilerResponse.testCaseResults().get(testCase2).output()));
    }

    @Test
    void whenUserSubmitsTheJavaSourceCodeWithCompilationIssueTheTheCompilationFails() throws IOException {
        // given
        compilerProperties = new CompilerProperties(
                "/",
                "input",
                "output",
                ".txt",
                true,
                "/Users/adisingh16/learning/app",
                "/app",
                "/Users/adisingh16/learning/app"
        );
        compilerService = new CompilerService(compilerProperties,
                newCachedThreadPool());

        var sourceCode = readString(Path.of("src/test/resources/sourcecode/sum/sum-with-compilation-error.java"));
        var input1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase1_input.txt"));
        var output1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase_output.txt"));
        var input2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_input.txt"));
        var output2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_output.txt"));
        var userId = randomUUID().toString();
        var  problemId = randomUUID().toString();

        // when
        String testCase1 = "testCase1";
        String testCase2 = "testCase2";
        final var submission = new Submission(userId,
                problemId,
                sourceCode,
                JAVA,
                Map.of(testCase1, new TestCase(input1, output1),
                        testCase2, new TestCase(input2, output2)
                ));
        var compilerResponse = compilerService.compileAndExecute(submission);

        // then
        assertEquals(FAILURE, compilerResponse.status());
    }

    @Test
    void whenUserSubmitsTheJavaSourceCodeWithNoCompilationIssueButTestCasesFail() throws IOException {
        // given
        compilerProperties = new CompilerProperties(
                "/",
                "input",
                "output",
                ".txt",
                true,
                "/Users/adisingh16/learning/app",
                "/app",
                "/Users/adisingh16/learning/app"
        );
        compilerService = new CompilerService(compilerProperties,
                newCachedThreadPool());

        var sourceCode = readString(Path.of("src/test/resources/sourcecode/sum/sum-with-logic-error.java"));
        var input1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase1_input.txt"));
        var output1 = readString(Path.of("src/test/resources/sourcecode/sum/testCase_output.txt"));
        var input2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_input.txt"));
        var output2 = readString(Path.of("src/test/resources/sourcecode/sum/testCase2_output.txt"));
        var userId = randomUUID().toString();
        var  problemId = randomUUID().toString();

        // when
        String testCase1 = "testCase1";
        String testCase2 = "testCase2";
        final var submission = new Submission(userId,
                problemId,
                sourceCode,
                JAVA,
                Map.of(testCase1, new TestCase(input1, output1),
                        testCase2, new TestCase(input2, output2)
                ));
        var compilerResponse = compilerService.compileAndExecute(submission);

        // then
        assertEquals(FAILURE, compilerResponse.testCaseResults().get(testCase1).status());
        assertEquals(FAILURE, compilerResponse.testCaseResults().get(testCase2).status());
    }
}
