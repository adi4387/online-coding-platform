package com.ps.tga.problem.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.EqualsAndHashCode.Include;
import org.springframework.data.annotation.*;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Document
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
@Schema
@Validated
public class Problem {

    private static final String OBJECT = "problem";

    @Id
    @QueryIndexed
    @NotEmpty(message = "is missing")
    @Include
    @Schema(name = "id", description = "The problem id")
    private String id;

    @NotEmpty(message = "is missing")
    @Schema(name = "name", description = "The problem name")
    private String name;

    @Field
    @NotEmpty(message = "is missing")
    @Schema(name = "description", description = "The problem description")
    private String description;

    @Field
    @NotEmpty(message = "is missing")
    @Schema(name = "example", description = "The problem example")
    private String example;

    @Field
    @NotEmpty(message = "is missing")
    @Schema(name = "constraints", description = "The problem constraints")
    private String constraints;

    @Field
    @Schema(name = "topic", description = "The problem topic")
    @NotNull(message = "is missing")
    private Topic topic;

    @Field
    private List<Tag> tags;

    @Field
    @NotEmpty(message = "is missing")
    private Map<String, TestCase> testCases;

    @Version
    @Schema(accessMode = READ_ONLY)
    private Long version;

    @CreatedDate
    @Schema(accessMode = READ_ONLY)
    private Long createdDate;

    @CreatedBy
    @Schema(accessMode = READ_ONLY)
    private String createdBy;

    @LastModifiedDate
    @Schema(accessMode = READ_ONLY)
    private Long lastModifiedDate;

    @LastModifiedBy
    @Schema(accessMode = READ_ONLY)
    private String lastModifiedBy;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Tag {

        @Field
        private String key;

        @Field
        private String value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TestCase {

        @Field
        private String input;

        @Field
        private String expectedOutput;
    }
}
