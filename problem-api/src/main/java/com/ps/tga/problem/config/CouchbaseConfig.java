package com.ps.tga.problem.config;

import com.couchbase.client.core.env.Authenticator;
import com.couchbase.client.core.env.PasswordAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.auditing.EnableCouchbaseAuditing;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;


@Configuration
@EnableCouchbaseAuditing
@EnableConfigurationProperties({CouchbaseProperties.class})
@EnableReactiveCouchbaseRepositories("com.ps.tga.problem.repository")
class CouchbaseConfig extends AbstractCouchbaseConfiguration {
    private final CouchbaseProperties props;
    private final String bucketName;
    private final String bucketTypeKey;

    @Autowired
    public CouchbaseConfig(
            CouchbaseProperties props,
            @Value("${spring.couchbase.bucket.name}") String bucketName,
            @Value("${spring.couchbase.bucket.type-key}") String typeKey) {
        this.props = props;
        this.bucketName = bucketName;
        this.bucketTypeKey = typeKey;
    }

    @Override
    protected Authenticator authenticator() {
        return PasswordAuthenticator.builder()
                .username(getUserName())
                .password(getPassword())
                .onlyEnablePlainSaslMechanism()
                .build();
    }

    @Override
    public String getConnectionString() {
        return props.getConnectionString();
    }

    @Override
    public String getUserName() {
        return props.getUsername();
    }

    @Override
    public String getPassword() {
        return props.getPassword();
    }

    @Override
    public String getBucketName() {
        return bucketName;
    }

    @Override
    public String typeKey() {
        return bucketTypeKey;
    }
}
