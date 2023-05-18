package com.ps.tga.problem.service;

import com.ps.tga.problem.exception.ResourceNotFoundException;
import com.ps.tga.problem.models.Problem;
import com.ps.tga.problem.models.Topic;
import com.ps.tga.problem.repository.ProblemRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
@Timed
public class ProblemService {

    private final ProblemRepository problemRepository;

    public Mono<Problem> createProblem(final Problem problem) {
        return problemRepository.save(problem);
    }

    public Flux<Problem> findAllProblems(final Integer limit, final Integer offset) {
        return problemRepository.findAll(limit, offset);
    }

    public Flux<Problem> findAllProblemsByTopic(Topic topic, final Integer limit, final Integer offset) {
        return problemRepository.findByTopic(topic, limit, offset);
    }

    public Mono<Problem> findById(final String id) {
        return problemRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(format("Problem with %s does not exist", id))));
    }
}
