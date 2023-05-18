package com.ps.tga.compiler.services.interpretation;

import com.ps.tga.compiler.models.InterpreterResponse;
import com.ps.tga.compiler.models.Language;
import com.ps.tga.compiler.services.ContainerService;
import com.ps.tga.logger.LogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ps.tga.compiler.constants.CompilerConstants.*;
import static com.ps.tga.compiler.models.CompilerStatus.FAILURE;
import static com.ps.tga.compiler.models.CompilerStatus.SUCCESS;
import static com.ps.tga.compiler.models.Language.PYTHON;

@Slf4j
@Component("pythonInterpreter")
@RequiredArgsConstructor
public class PythonInterpreter implements Interpreter {

    private final ContainerService containerService;

    @Override
    public InterpreterResponse interpret(String executionPath, String testCaseFileName) {

        var processOutput = containerService.runContainer(PYTHON_INTERPRETER_IMAGE_NAME,
                PYTHON_INTERPRETER_CONTAINER_NAME,
                DEFAULT_TIMEOUT,
                Map.of(
                        SOURCE_CODE_FILE_NAME, PYTHON.interpretedCodeFileName(),
                        TEST_CASE_FILE_NAME, testCaseFileName,
                        EXECUTION_PATH, executionPath
                )
        );

        var status = processOutput.status() == 0 ? SUCCESS : FAILURE;

        log.info(
                LogEvent.eventType(EXECUTE_CODE)
                        .with(STATUS, status)
                        .with(LANGUAGE, getLanguage())
                        .with(EXECUTION_PATH, executionPath)
                        .with(TEST_CASE_FILE_NAME, testCaseFileName)
                        .asJSON());
        return new InterpreterResponse(
                processOutput.executionDuration(),
                processOutput.stdOut() + processOutput.stdErr(),
                status
        );
    }

    @Override
    public Language getLanguage() {
        return PYTHON;
    }
}
