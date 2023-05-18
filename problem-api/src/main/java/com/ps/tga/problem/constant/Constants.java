package com.ps.tga.problem.constant;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public abstract class Constants {

    public static final String CREATE_PROBLEM = "createProblem";
    public static final String GET_PROBLEMS = "getProblems";
    public static final String GET_PROBLEMS_BY_TOPIC = "getProblemsByTopic";
    public static final String GET_PROBLEM_BY_ID = "getProblemByID";
    public static final String PROBLEMS = "/problems";
    public static final String STATUS = "status";
    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";
    public static final String TOPIC = "topic";
    public enum RequestStatus {
        PENDING, SUCCESS, FAILURE
    }
}
