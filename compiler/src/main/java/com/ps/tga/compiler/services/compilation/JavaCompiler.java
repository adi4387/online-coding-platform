package com.ps.tga.compiler.services.compilation;

import com.ps.tga.compiler.models.CompilationResponse;
import com.ps.tga.compiler.models.Language;
import com.ps.tga.compiler.services.ContainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.ps.tga.compiler.constants.CompilerConstants.*;
import static com.ps.tga.compiler.models.CompilerStatus.FAILURE;
import static com.ps.tga.compiler.models.CompilerStatus.SUCCESS;
import static com.ps.tga.compiler.models.Language.JAVA;

@Slf4j
@Service("javaCompiler")
@RequiredArgsConstructor
public class JavaCompiler implements Compiler {

    private final ContainerService containerService;

    @Override
    public CompilationResponse compile(String executionPath) {
        var processOutput = containerService.runContainer(JAVA_COMPILER_IMAGE_NAME,
                JAVA_COMPILER_CONTAINER_NAME,
                DEFAULT_TIMEOUT,
                Map.of(SOURCE_CODE_FILE_NAME, JAVA.sourceCodeFileName(),
                        EXECUTION_PATH, executionPath
                )
        );
        return new CompilationResponse(processOutput.executionDuration(),
                processOutput.stdOut() + processOutput.stdErr(),
                processOutput.status() == 0 ? SUCCESS: FAILURE);
    }

    @Override
    public Language getLanguage() {
        return JAVA;
    }
}
