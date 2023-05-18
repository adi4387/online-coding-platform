package com.ps.tga.problem.repository;

import com.ps.tga.problem.models.Problem;
import com.ps.tga.problem.models.Topic;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProblemRepository extends ReactiveCouchbaseRepository<Problem, String> {

    @Query(
            value = "SELECT META(#{#n1ql.bucket}).id AS __id, META(#{#n1ql.bucket}).cas AS __cas, #{#n1ql.bucket}.* FROM #{#n1ql.bucket} where #{#n1ql.filter} ORDER BY name DESC LIMIT $1 OFFSET $2"
    )
    Flux<Problem> findAll(final Integer limit, final Integer offset);

    @Query(
            value = "SELECT META(#{#n1ql.bucket}).id AS __id, META(#{#n1ql.bucket}).cas AS __cas, #{#n1ql.bucket}.* FROM #{#n1ql.bucket} where #{#n1ql.filter} and topic = $1 ORDER BY name DESC LIMIT $2 OFFSET $3"
    )
    Flux<Problem> findByTopic(Topic topic, final Integer limit, final Integer offset);
}
