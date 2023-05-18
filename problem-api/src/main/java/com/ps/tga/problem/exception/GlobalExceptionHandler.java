package com.ps.tga.problem.exception;

import com.couchbase.client.core.error.DocumentExistsException;
import com.ps.tga.logger.LogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public Mono<ResponseEntity<ErrorResponse>> handle(Exception ex) {
        log.error(LogEvent.unexpectedError(ex).asJSON());
        final String errorMessage = ex.getMessage();
        return Mono.just(
                new ResponseEntity<>(
                        new ErrorResponse(INTERNAL_SERVER_ERROR.value(), errorMessage),
                        INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ErrorResponse> handle(ResourceNotFoundException ex) {
        final String errorMessage = ex.getMessage();
        log.error(LogEvent.unexpectedError(ex).asJSON());

        return new ResponseEntity<>(
                new ErrorResponse(NOT_FOUND.value(), errorMessage),
                NOT_FOUND);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> handle(DocumentExistsException ex) {
        log.error(LogEvent.unexpectedError(ex).asJSON());

        return new ResponseEntity<>(
                new ErrorResponse(UNPROCESSABLE_ENTITY.value(), "Document with the given id already exists."),
                UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handle(final WebExchangeBindException ex) {
        log.error(LogEvent.unexpectedError(ex).asJSON());
        return new ResponseEntity<>(new ErrorResponse(BAD_REQUEST.value(), ex.getFieldErrors().stream().sorted(comparing(FieldError::getField)).map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage()).collect(Collectors.joining(","))), BAD_REQUEST);
    }
}
