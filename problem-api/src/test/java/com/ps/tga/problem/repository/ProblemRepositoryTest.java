package com.ps.tga.problem.repository;

import com.ps.tga.problem.config.AbstractCouchbaseIntegrationTest;
import com.ps.tga.problem.models.Problem;
import com.ps.tga.problem.models.Problem.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.ps.tga.logger.JsonFactory.asObject;
import static com.ps.tga.problem.models.Topic.ALGORITHM;
import static com.ps.tga.problem.models.Topic.PROBLEM_SOLVING;
import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@Slf4j
@SpringBootTest
@TestInstance(PER_CLASS)
class ProblemRepositoryTest extends AbstractCouchbaseIntegrationTest {

    @Autowired
    ProblemRepository problemRepository;

    @BeforeAll
    void setUp() {
        List<String> problemPaths = List.of(
                "src/test/resources/json/simple-array-sum.json",
                "src/test/resources/json/sum-two-numbers.json",
                "src/test/resources/json/multiply-two-numbers.json"
        );
        problemPaths.forEach(problemPath -> persist(problemPath).block());
    }

    @AfterAll
    public void tearDown() {
        problemRepository.deleteAll().block();
        couchbaseContainer.stop();
    }

    @SneakyThrows
    private Mono<Problem> persist(String problemPath) {
        return problemRepository.save((Problem) asObject(readProblemsFromFile(problemPath), Problem.class));
    }

    @SneakyThrows
    private String readProblemsFromFile(String path) {
        return readString(Path.of(path));
    }

    @SneakyThrows
    @Test
    void whenProblemIsSubmittedThenItIsPersisted() {
        var problemPath = "src/test/resources/json/compare-the-triplets.json";
        var problem = Problem.builder().id("compare-the-triplets").name("Compare the triplets").description("Alice and Bob each created one problem for HackerRank. A reviewer rates the two challenges, awarding points on a scale from 1 to 100 for three categories: problem clarity, originality, and difficulty.\n\nThe rating for Alice's challenge is the triplet a = (a[0], a[1], a[2]), and the rating for Bob's challenge is the triplet b = (b[0], b[1], b[2]).The task is to find their comparison points by comparing a[0] with b[0], a[1] with b[1], and a[2] with b[2].\n\nIf a[i] > b[i], then Alice is awarded 1 point.\nIf a[i] < b[i], then Bob is awarded 1 point.\nIf a[i] = b[i], then neither person receives a point.\nComparison points is the total points a person earned.\n\nGiven a and b, determine their respective comparison points.").example("For example, a = [1, 2, 3], b = [3, 2, 1], For elements *0*, Bob is awarded a point because a[0] .\nFor the equal elements a[1] and b[1], no points are earned.\nFinally, for elements 2, a[2] > b[2] so Alice receives a point.\nThe return array is [1, 1] with Alice's score first and Bob's second.").constraints("1<=a[i]<=1000\n1<=b[i]<=1000").topic(PROBLEM_SOLVING).tags(List.of(Tag.builder().key("difficultyLevel").value("Easy").build(), Tag.builder().key("maxScore").value("10").build())).testCases(Map.of("testCase1", Problem.TestCase.builder().input("5 6 7\n3 6 10").expectedOutput("1 1").build())).build();
        StepVerifier.create(persist(problemPath)).expectNext(problem).verifyComplete();
    }

    @Test
    void givenProblemsExistInSystemWhenUserFetchesListOfProblemsThenLimitedNumberIsReturned() {
        // given // when // then
        StepVerifier.create(problemRepository.findAll(2, 0))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void givenProblemsExistInSystemWhenUserFetchesListOfProblemsByTopicThenLimitedNumberIsReturned() {
        // given // when // then
        StepVerifier.create(problemRepository.findByTopic(ALGORITHM, 2, 0))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void givenProblemsExistInSystemWhenUserFetchesProblemByIdThenThatIsReturned() {
        // given // when // then
        StepVerifier.create(problemRepository.findById("compare-the-triplets"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void givenProblemsDoesNotExistInSystemWhenUserFetchesProblemByIdThenEmptyIsReturned() {
        // given // when // then
        StepVerifier.create(problemRepository.findById("divide-two-numbers"))
                .verifyComplete();
    }
}
