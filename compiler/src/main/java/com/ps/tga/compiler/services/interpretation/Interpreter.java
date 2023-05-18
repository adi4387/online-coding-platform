package com.ps.tga.compiler.services.interpretation;

import com.ps.tga.compiler.models.InterpreterResponse;
import com.ps.tga.compiler.models.Language;

public interface Interpreter {

    InterpreterResponse interpret(final String executionPath, final String testCaseFileName);

    Language getLanguage();
}
