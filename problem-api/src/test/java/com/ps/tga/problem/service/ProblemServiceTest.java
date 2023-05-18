package com.ps.tga.problem.service;

import com.ps.tga.problem.models.Problem;
import com.ps.tga.problem.models.Problem.Tag;
import com.ps.tga.problem.models.Topic;
import com.ps.tga.problem.repository.ProblemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static com.ps.tga.problem.models.Topic.ALGORITHM;
import static com.ps.tga.problem.models.Topic.PROBLEM_SOLVING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ProblemServiceTest {

    @Mock
    protected ProblemRepository problemRepository;

    @InjectMocks
    protected ProblemService problemService;

    @Test
    void whenUserSubmitsAProblemTheProblemIsPersisted() {
        // given
        var problem = Problem.builder().id("compare-the-triplets").name("Compare the triplets").description("Alice and Bob each created one problem for HackerRank. A reviewer rates the two challenges, awarding points on a scale from 1 to 100 for three categories: problem clarity, originality, and difficulty.\n\nThe rating for Alice's challenge is the triplet a = (a[0], a[1], a[2]), and the rating for Bob's challenge is the triplet b = (b[0], b[1], b[2]).The task is to find their comparison points by comparing a[0] with b[0], a[1] with b[1], and a[2] with b[2].\n\nIf a[i] > b[i], then Alice is awarded 1 point.\nIf a[i] < b[i], then Bob is awarded 1 point.\nIf a[i] = b[i], then neither person receives a point.\nComparison points is the total points a person earned.\n\nGiven a and b, determine their respective comparison points.").example("For example, a = [1, 2, 3], b = [3, 2, 1], For elements *0*, Bob is awarded a point because a[0] .\nFor the equal elements a[1] and b[1], no points are earned.\nFinally, for elements 2, a[2] > b[2] so Alice receives a point.\nThe return array is [1, 1] with Alice's score first and Bob's second.").constraints("1<=a[i]<=1000\n1<=b[i]<=1000").topic(PROBLEM_SOLVING).tags(List.of(Tag.builder().key("difficultyLevel").value("Easy").build(), Tag.builder().key("maxScore").value("10").build())).testCases(Map.of("testCase1", Problem.TestCase.builder().input("5 6 7\n3 6 10").expectedOutput("1 1").build())).build();
        when(problemRepository.save(any(Problem.class))).thenReturn(Mono.just(problem));

        // when then
        StepVerifier.create(problemService.createProblem(problem))
                .expectNext(problem)
                .verifyComplete();
    }

    @Test
    void whenUserRequestsForProblemsTheProblemsAreReturnedInPaginatedManner() {
        // given
        var compareTheTriplets = Problem.builder().id("compare-the-triplets").build();
        var sumTwoNumbers = Problem.builder().id("sum-two-numbers").build();
        var multipleTwoNumbers = Problem.builder().id("multiple-two-numbers").build();
        var simpleArraySum = Problem.builder().id("simple-array-sum").build();
        when(problemRepository.findAll(anyInt(), anyInt())).thenReturn(Flux.fromIterable(List.of(compareTheTriplets, sumTwoNumbers, multipleTwoNumbers, simpleArraySum)));

        // when then
        StepVerifier.create(problemService.findAllProblems(2, 0))
                .expectNext(compareTheTriplets)
                .expectNext(sumTwoNumbers)
                .expectNext(multipleTwoNumbers)
                .expectNext(simpleArraySum)
                .verifyComplete();
    }

    @Test
    void whenUserRequestsForProblemsByTopicTheProblemsAreReturnedInPaginatedManner() {
        // given
        var sumTwoNumbers = Problem.builder().id("sum-two-numbers").build();
        when(problemRepository.findByTopic(any(Topic.class), anyInt(), anyInt())).thenReturn(Flux.just(sumTwoNumbers));

        // when then
        StepVerifier.create(problemService.findAllProblemsByTopic(ALGORITHM, 2, 0))
                .expectNext(sumTwoNumbers)
                .verifyComplete();
    }

    @Test
    void givenProblemExistsWhenUserRequestsForProblemsByIdThenItReturnedInPaginatedManner() {
        // given
        String id = "sum-two-numbers";
        var sumTwoNumbers = Problem.builder().id(id).build();
        when(problemRepository.findById(id)).thenReturn(Mono.just(sumTwoNumbers));

        // when then
        StepVerifier.create(problemService.findById(id))
                .expectNext(sumTwoNumbers)
                .verifyComplete();
    }
}
