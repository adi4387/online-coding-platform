package com.ps.tga.problem.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.time.Duration.ofSeconds;
import static org.testcontainers.containers.wait.strategy.Wait.forHealthcheck;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Testcontainers
public abstract class AbstractCouchbaseIntegrationTest {

    private static final String username = "Administrator";
    private static final String password = "password";
    private static final String bucketName = "online-coding-platform";

    @Container
    protected static final CouchbaseContainer couchbaseContainer =
            new CouchbaseContainer("couchbase/server:community-7.1.1")
                    .withExposedPorts(8091)
                    .withCredentials(username, password)
                    .withBucket(new BucketDefinition(bucketName).withPrimaryIndex(true))
                    .withStartupTimeout(ofSeconds(90))
                    .waitingFor(forHealthcheck());
    private static final String bucketTypeKey = "_class";

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        couchbaseContainer.start();
        await().until(couchbaseContainer::isRunning);
        registry.add("spring.couchbase.connection-string", couchbaseContainer::getConnectionString);
        registry.add("spring.couchbase.username", couchbaseContainer::getUsername);
        registry.add("spring.couchbase.password", couchbaseContainer::getPassword);
        registry.add("spring.couchbase.bucket.name", () -> bucketName);
        registry.add("spring.couchbase.bucket.type-key", () -> bucketTypeKey);
        registry.add("logging.level.org.springframework.data.couchbase.repository.query", () -> "DEBUG");
    }
}
