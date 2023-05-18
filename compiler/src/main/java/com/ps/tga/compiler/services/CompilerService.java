package com.ps.tga.compiler.services;

import com.ps.tga.compiler.models.*;
import com.ps.tga.logger.LogEvent;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.ps.tga.compiler.constants.CompilerConstants.*;
import static com.ps.tga.compiler.models.CompilerStatus.*;
import static com.ps.tga.compiler.services.compilation.CompilerFactory.getCompiler;
import static com.ps.tga.compiler.services.interpretation.InterpreterFactory.getInterpreter;
import static com.ps.tga.logger.LogEvent.unexpectedError;
import static java.nio.file.Files.writeString;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.springframework.util.FileSystemUtils.deleteRecursively;

@Slf4j
@RequiredArgsConstructor
public class CompilerService {
    private final CompilerProperties compilerProperties;
    private final ExecutorService threadPool;

    @Timed
    public CompilerResponse compileAndExecute(final Submission submission) throws IOException {
        String executionPathOnHost = createExecutionDirectory(submission, compilerProperties.hostMountedPath());
        String executionPathInsideContainer = createExecutionDirectory(submission, compilerProperties.containerMountedPath());

        try {
            createMainInputOutputFiles(submission, executionPathOnHost);

            Language language = submission.language();
            log.info(
                    LogEvent.eventType(COMPILE_CODE)
                            .with(STATUS, PENDING)
                            .with(LANGUAGE, language)
                            .with(EXECUTION_PATH_ON_HOST, executionPathOnHost)
                            .with(EXECUTION_PATH_INSIDE_CONTAINER, executionPathInsideContainer)
                            .asJSON());
            var compilationResponse = compileSourceCode(language, executionPathInsideContainer);

            if(SUCCESS.equals(compilationResponse.status())) {
                log.info(
                        LogEvent.eventType(COMPILE_CODE)
                                .with(STATUS, SUCCESS)
                                .with(LANGUAGE, language)
                                .with(EXECUTION_PATH_ON_HOST, executionPathOnHost)
                                .with(EXECUTION_PATH_INSIDE_CONTAINER, executionPathInsideContainer)
                                .asJSON());
                Map<String, TestCaseResult> testCaseResults = createAndRunTestCases(executionPathOnHost, executionPathInsideContainer, submission.testCases(), language);
                return new CompilerResponse(compilationResponse.compilationDuration(), compilationResponse.message(), compilationResponse.status(), testCaseResults);
            } else {
                log.info(
                        LogEvent.eventType(COMPILE_CODE)
                                .with(STATUS, FAILURE)
                                .with(LANGUAGE, language)
                                .with(EXECUTION_PATH_ON_HOST, executionPathOnHost)
                                .with(EXECUTION_PATH_INSIDE_CONTAINER, executionPathInsideContainer)
                                .asJSON());
            }
            return new CompilerResponse(compilationResponse.compilationDuration(), compilationResponse.message(), compilationResponse.status(), Map.of());
        } finally {
            asyncDeleteSourceCodeDirectoryForUser(submission.userId());
        }
    }

    private String createExecutionDirectory(Submission submission, String path) {
        return path
                + compilerProperties.pathSeparator()
                + submission.userId()
                + compilerProperties.pathSeparator()
                + submission.problemId()
                + compilerProperties.pathSeparator()
                + submission.language().sourceCodeFolder();

    }

    private void createMainInputOutputFiles(Submission submission, String executionPathOnHost) throws IOException {
        Files.createDirectories(Path.of(executionPathOnHost));
        writeString(Path.of(executionPathOnHost + compilerProperties.pathSeparator() + submission.language().sourceCodeFileName()), submission.sourceCode(), CREATE);
    }

    private Map<String, TestCaseResult> createAndRunTestCases(String executionPathOnHost, String executionPathInsideContainer, Map<String, TestCase> testCaseMap, Language language) {
        var testCaseResults = new LinkedHashMap<String, TestCaseResult>();
        try {
            for (Map.Entry<String, TestCase> entry : testCaseMap.entrySet()) {
                String id = entry.getKey();
                TestCase testCase = entry.getValue();
                String testCaseInputFileName = id + "_" + compilerProperties.inputFileName() + compilerProperties.inputOutputFileExtension();
                writeString(Path.of(executionPathOnHost + compilerProperties.pathSeparator() + testCaseInputFileName), testCase.input(), CREATE);
                writeString(Path.of(executionPathOnHost + compilerProperties.pathSeparator() + id + "_" + compilerProperties.expectedOutputFileName() + compilerProperties.inputOutputFileExtension()), testCase.expectedOutput(), CREATE);
                var interpreterResponse = interpretSourceCode(language, executionPathInsideContainer, testCaseInputFileName);
                testCaseResults.put(id,
                        new TestCaseResult(
                                testCase.input(),
                                testCase.expectedOutput(),
                                interpreterResponse.output(),
                                testCase.expectedOutput().equals(interpreterResponse.output()) ? SUCCESS:FAILURE,
                                interpreterResponse.compilationDuration()
                        )
                );
            }
        } catch (Exception exception) {
           log.error(unexpectedError(exception).asJSON());
        }
        return testCaseResults;
    }

    void asyncDeleteSourceCodeDirectoryForUser(String userId) {
        Path path = Path.of(compilerProperties.hostMountedPath() + compilerProperties.pathSeparator() + userId);
        if (compilerProperties.deleteSourceCode()) {
            threadPool.submit(() -> deleteRecursively(path));
            log.info(LogEvent.eventType("userDirectoryDeleted").with("path", path).asJSON());
        }
    }

    private CompilationResponse compileSourceCode(Language language, String executionPath) {
        var compiler = getCompiler(language);
        if (null != compiler) {
            return compiler.compile(executionPath);
        } else {
            return new CompilationResponse(0L, "Compilation skipped", SUCCESS);
        }
    }

    private InterpreterResponse interpretSourceCode(Language language, String executionPath, String testCaseFileName) {
        return getInterpreter(language).interpret(executionPath, testCaseFileName);
    }
}
