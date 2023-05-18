package com.ps.tga.compiler.services.interpretation;

import com.ps.tga.compiler.models.Language;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class InterpreterFactory {

    private static final Map<Language, Interpreter> registeredInterpreters = new EnumMap<>(Language.class);

    public static void registerInterpreter(Language language, Interpreter interpreter) {
        registeredInterpreters.putIfAbsent(language, interpreter);
    }

    public static Interpreter getInterpreter(Language language) {
        return registeredInterpreters.get(language);
    }
}
