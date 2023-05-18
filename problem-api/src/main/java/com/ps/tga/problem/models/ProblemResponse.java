package com.ps.tga.problem.models;

import java.util.List;

public record ProblemResponse(List<Problem> data) {
    private static final String OBJECT = "list";
}
