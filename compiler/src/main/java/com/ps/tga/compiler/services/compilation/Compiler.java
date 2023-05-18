package com.ps.tga.compiler.services.compilation;

import com.ps.tga.compiler.models.CompilationResponse;
import com.ps.tga.compiler.models.Language;

public interface Compiler {

    CompilationResponse compile(final String executionPath);

    Language getLanguage();
}
