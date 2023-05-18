package com.ps.tga.compiler.services;

import com.ps.tga.compiler.services.compilation.JavaCompiler;
import com.ps.tga.compiler.services.interpretation.JavaInterpreter;
import com.ps.tga.compiler.services.interpretation.PythonInterpreter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.ps.tga.compiler.models.Language.JAVA;
import static com.ps.tga.compiler.models.Language.PYTHON;
import static com.ps.tga.compiler.services.compilation.CompilerFactory.registerExecutionType;
import static com.ps.tga.compiler.services.interpretation.InterpreterFactory.registerInterpreter;
import static java.util.concurrent.Executors.newCachedThreadPool;

@Configuration
@EnableConfigurationProperties(value = { CompilerProperties.class })
public class CompilerConfig {

    @Bean
    CompilerService compilerService(final CompilerProperties compilerProperties,
                                    final ContainerService containerService) {
        registerCompilers(containerService);
        registerInterpreters(containerService);
        return new CompilerService(compilerProperties,
                newCachedThreadPool());
    }

    private void registerCompilers(final ContainerService containerService) {
        registerExecutionType(JAVA, new JavaCompiler(containerService));
    }

    private void registerInterpreters(final ContainerService containerService) {
        registerInterpreter(JAVA, new JavaInterpreter(containerService));
        registerInterpreter(PYTHON, new PythonInterpreter(containerService));
    }
}
