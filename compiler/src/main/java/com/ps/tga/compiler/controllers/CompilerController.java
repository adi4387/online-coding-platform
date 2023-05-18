package com.ps.tga.compiler.controllers;

import com.ps.tga.compiler.models.CompilerResponse;
import com.ps.tga.compiler.models.Submission;
import com.ps.tga.compiler.services.CompilerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.ok;

/**
 * The compiler controller class to compile a submission
 *
 * @author adisingh16
 */
@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class CompilerController {

    private final CompilerService compilerService;

    /**
     *
     * @param submission the submission by client
     * @return the submission request result location in header with 202 status code
     */
    @PostMapping("/compile")
    @Operation(
            summary = "The submission will be compiled and run against input and validated against output"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The submission request is ok."),
            @ApiResponse(responseCode = "400", description = "The submission request is invalid."),
            @ApiResponse(responseCode = "500", description = "There is some issue with the server")
    })
    public ResponseEntity<CompilerResponse> compile(@Valid @RequestBody Submission submission) {
        try {
            return ok(compilerService.compileAndExecute(submission));
        } catch (Exception e) {
            return internalServerError().build();
        }
    }
}
