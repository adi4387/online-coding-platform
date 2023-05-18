package com.ps.tga.compiler.services.compilation;

import com.ps.tga.compiler.models.Language;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CompilerFactory {

    private static final Map<Language, Compiler> registeredCompilers = new EnumMap<>(Language.class);

    public static void registerExecutionType(Language language, Compiler compiler) {
        registeredCompilers.putIfAbsent(language, compiler);
    }

    public static Compiler getCompiler(Language language) {
        return registeredCompilers.get(language);
    }
}
