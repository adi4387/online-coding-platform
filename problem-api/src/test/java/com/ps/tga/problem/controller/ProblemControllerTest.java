package com.ps.tga.problem.controller;

import com.ps.tga.problem.models.Problem;
import com.ps.tga.problem.models.Problem.Tag;
import com.ps.tga.problem.models.Problem.TestCase;
import com.ps.tga.problem.models.ProblemResponse;
import com.ps.tga.problem.models.Topic;
import com.ps.tga.problem.service.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.ps.tga.problem.constant.Constants.PROBLEMS;
import static com.ps.tga.problem.models.Topic.PROBLEM_SOLVING;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@WebFluxTest(controllers = {ProblemController.class})
@AutoConfigureWebTestClient(timeout = "10000")
class ProblemControllerTest {

    @Autowired
    protected WebTestClient webTestClient;

    @MockBean
    protected ProblemService problemService;

    @Test
    void whenUserCreatesAProblemWithCorrectPayloadThenReturn201WithLocationInHeader() {
        // given
        var problem = Problem.builder()
                .id("compare-the-triplets")
                .name("Compare the triplets")
                .description("Alice and Bob each created one problem for HackerRank. A reviewer rates the two challenges, awarding points on a scale from 1 to 100 for three categories: problem clarity, originality, and difficulty.\n\nThe rating for Alice's challenge is the triplet a = (a[0], a[1], a[2]), and the rating for Bob's challenge is the triplet b = (b[0], b[1], b[2]).The task is to find their comparison points by comparing a[0] with b[0], a[1] with b[1], and a[2] with b[2].\n\nIf a[i] > b[i], then Alice is awarded 1 point.\nIf a[i] < b[i], then Bob is awarded 1 point.\nIf a[i] = b[i], then neither person receives a point.\nComparison points is the total points a person earned.\n\nGiven a and b, determine their respective comparison points.")
                .example("For example, a = [1, 2, 3], b = [3, 2, 1], For elements *0*, Bob is awarded a point because a[0] .\nFor the equal elements a[1] and b[1], no points are earned.\nFinally, for elements 2, a[2] > b[2] so Alice receives a point.\nThe return array is [1, 1] with Alice's score first and Bob's second.")
                .constraints("1<=a[i]<=1000\n1<=b[i]<=1000")
                .topic(PROBLEM_SOLVING)
                .tags(List.of(Tag.builder().key("difficultyLevel").value("Easy").build(), Tag.builder().key("maxScore").value("10").build()))
                .testCases(Map.of("testCase1", TestCase.builder().input("5 6 7\n3 6 10").expectedOutput("1 1").build()))
                .build();

        when(problemService.createProblem(any(Problem.class))).thenReturn(Mono.just(problem));

        // when then
        webTestClient.post()
                .uri("/problems")
                .header("TraceId", randomUUID().toString())
                .contentType(APPLICATION_JSON)
                .body(fromValue(problem))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .location(PROBLEMS + "/" + problem.getId());
    }

    @Test
    void whenUserWantsListOfProblemsThenReturnLimitedListOfProblems() {
        // given
        var problem = Problem.builder()
                .id("compare-the-triplets")
                .name("Compare the triplets")
                .description("Alice and Bob each created one problem for HackerRank. A reviewer rates the two challenges, awarding points on a scale from 1 to 100 for three categories: problem clarity, originality, and difficulty.\n\nThe rating for Alice's challenge is the triplet a = (a[0], a[1], a[2]), and the rating for Bob's challenge is the triplet b = (b[0], b[1], b[2]).The task is to find their comparison points by comparing a[0] with b[0], a[1] with b[1], and a[2] with b[2].\n\nIf a[i] > b[i], then Alice is awarded 1 point.\nIf a[i] < b[i], then Bob is awarded 1 point.\nIf a[i] = b[i], then neither person receives a point.\nComparison points is the total points a person earned.\n\nGiven a and b, determine their respective comparison points.")
                .example("For example, a = [1, 2, 3], b = [3, 2, 1], For elements *0*, Bob is awarded a point because a[0] .\nFor the equal elements a[1] and b[1], no points are earned.\nFinally, for elements 2, a[2] > b[2] so Alice receives a point.\nThe return array is [1, 1] with Alice's score first and Bob's second.")
                .constraints("1<=a[i]<=1000\n1<=b[i]<=1000")
                .topic(PROBLEM_SOLVING)
                .tags(List.of(Tag.builder().key("difficultyLevel").value("Easy").build(), Tag.builder().key("maxScore").value("10").build()))
                .testCases(Map.of("testCase1", TestCase.builder().input("5 6 7\n3 6 10").expectedOutput("1 1").build()))
                .build();


        when(problemService.findAllProblems(anyInt(), anyInt())).thenReturn(Flux.just(problem));

        var problemResponse = new ProblemResponse(List.of(problem));

        // when // then
        webTestClient.get()
                .uri("/problems")
                .header("TraceId", randomUUID().toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProblemResponse.class)
                .consumeWith(problemResponseEntityExchangeResult -> assertEquals(problemResponse, problemResponseEntityExchangeResult.getResponseBody()));
    }

    @Test
    void whenUserWantsListOfProblemsByTopicThenReturnLimitedListOfProblems() {
        // given
        var problem = Problem.builder()
                .id("compare-the-triplets")
                .name("Compare the triplets")
                .description("Alice and Bob each created one problem for HackerRank. A reviewer rates the two challenges, awarding points on a scale from 1 to 100 for three categories: problem clarity, originality, and difficulty.\n\nThe rating for Alice's challenge is the triplet a = (a[0], a[1], a[2]), and the rating for Bob's challenge is the triplet b = (b[0], b[1], b[2]).The task is to find their comparison points by comparing a[0] with b[0], a[1] with b[1], and a[2] with b[2].\n\nIf a[i] > b[i], then Alice is awarded 1 point.\nIf a[i] < b[i], then Bob is awarded 1 point.\nIf a[i] = b[i], then neither person receives a point.\nComparison points is the total points a person earned.\n\nGiven a and b, determine their respective comparison points.")
                .example("For example, a = [1, 2, 3], b = [3, 2, 1], For elements *0*, Bob is awarded a point because a[0] .\nFor the equal elements a[1] and b[1], no points are earned.\nFinally, for elements 2, a[2] > b[2] so Alice receives a point.\nThe return array is [1, 1] with Alice's score first and Bob's second.")
                .constraints("1<=a[i]<=1000\n1<=b[i]<=1000")
                .topic(PROBLEM_SOLVING)
                .tags(List.of(Tag.builder().key("difficultyLevel").value("Easy").build(), Tag.builder().key("maxScore").value("10").build()))
                .testCases(Map.of("testCase1", TestCase.builder().input("5 6 7\n3 6 10").expectedOutput("1 1").build()))
                .build();


        when(problemService.findAllProblemsByTopic(any(Topic.class), anyInt(), anyInt())).thenReturn(Flux.just(problem));

        var problemResponse = new ProblemResponse(List.of(problem));

        // when // then
        webTestClient.get()
                .uri("/problems/topic/ALGORITHM")
                .header("TraceId", randomUUID().toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProblemResponse.class)
                .consumeWith(problemResponseEntityExchangeResult -> assertEquals(problemResponse, problemResponseEntityExchangeResult.getResponseBody()));
    }

    @Test
    void whenUserWantsListOfProblemsByIdThenProblemIsReturned() {
        // given
        String id = "compare-the-triplets";
        var problem = Problem.builder()
                .id(id)
                .name("Compare the triplets")
                .description("Alice and Bob each created one problem for HackerRank. A reviewer rates the two challenges, awarding points on a scale from 1 to 100 for three categories: problem clarity, originality, and difficulty.\n\nThe rating for Alice's challenge is the triplet a = (a[0], a[1], a[2]), and the rating for Bob's challenge is the triplet b = (b[0], b[1], b[2]).The task is to find their comparison points by comparing a[0] with b[0], a[1] with b[1], and a[2] with b[2].\n\nIf a[i] > b[i], then Alice is awarded 1 point.\nIf a[i] < b[i], then Bob is awarded 1 point.\nIf a[i] = b[i], then neither person receives a point.\nComparison points is the total points a person earned.\n\nGiven a and b, determine their respective comparison points.")
                .example("For example, a = [1, 2, 3], b = [3, 2, 1], For elements *0*, Bob is awarded a point because a[0] .\nFor the equal elements a[1] and b[1], no points are earned.\nFinally, for elements 2, a[2] > b[2] so Alice receives a point.\nThe return array is [1, 1] with Alice's score first and Bob's second.")
                .constraints("1<=a[i]<=1000\n1<=b[i]<=1000")
                .topic(PROBLEM_SOLVING)
                .tags(List.of(Tag.builder().key("difficultyLevel").value("Easy").build(), Tag.builder().key("maxScore").value("10").build()))
                .testCases(Map.of("testCase1", TestCase.builder().input("5 6 7\n3 6 10").expectedOutput("1 1").build()))
                .build();


        when(problemService.findById(id)).thenReturn(Mono.just(problem));

        // when // then
        webTestClient.get()
                .uri("/problems/" + id)
                .header("TraceId", randomUUID().toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Problem.class)
                .consumeWith(problemResponseEntityExchangeResult -> assertEquals(problem, problemResponseEntityExchangeResult.getResponseBody()));
    }

}
