package com.ps.tga.compiler.controllers;

import com.ps.tga.compiler.models.Submission;
import com.ps.tga.compiler.models.TestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.ps.tga.compiler.models.Language.JAVA;
import static com.ps.tga.logger.JsonFactory.asString;
import static java.util.UUID.randomUUID;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class CompilerControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    void whenJavaSolutionIsSubmittedByClientAcceptTheRequestAndReturnBackTheLocationOfTheResource() throws Exception {
        // given
        var sourceCode = Files.readString(Path.of("src/test/resources/sourcecode/sum/sum.java"));
        var input1 = Files.readString(Path.of("src/test/resources/sourcecode/sum/testCase1_input.txt"));
        var output1 = Files.readString(Path.of("src/test/resources/sourcecode/sum/testCase_output.txt"));
        var input2 = Files.readString(Path.of("src/test/resources/sourcecode/sum/testCase2_input.txt"));
        var output2 = Files.readString(Path.of("src/test/resources/sourcecode/sum/testCase2_output.txt"));
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

        mockMvc.perform(post("/submissions/compile").content(asString(submission)).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenPythonSolutionIsSubmittedByClientAcceptTheRequestAndReturnBackTheLocationOfTheResource() throws Exception {
        // given
        var sourceCode = Files.readString(Path.of("src/test/resources/sourcecode/sum/sum.java"));
        var input1 = Files.readString(Path.of("src/test/resources/sourcecode/sum/testCase1_input.txt"));
        var output1 = Files.readString(Path.of("src/test/resources/sourcecode/sum/testCase_output.txt"));
        var input2 = Files.readString(Path.of("src/test/resources/sourcecode/sum/testCase2_input.txt"));
        var output2 = Files.readString(Path.of("src/test/resources/sourcecode/sum/testCase2_output.txt"));
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

        mockMvc.perform(post("/submissions/compile").content(asString(submission)).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
