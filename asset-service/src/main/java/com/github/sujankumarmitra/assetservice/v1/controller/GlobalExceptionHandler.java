package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.assetservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.assetservice.v1.exception.DefaultErrorDetails;
import com.github.sujankumarmitra.assetservice.v1.exception.ErrorDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@RestControllerAdvice
//@ApiResponse
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiOperationException.class)
    public Mono<ResponseEntity<ErrorResponse>> apiExceptionHandler(ApiOperationException ex) {
        return Mono
                .just(ex.getErrors())
                .map(ErrorResponse::new)
                .map(badRequest()::body);
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ErrorResponse>> apiExceptionHandler(Throwable th) {
        return Mono
                .just(th.getMessage())
                .map(DefaultErrorDetails::new)
                .cast(ErrorDetails.class)
                .map(Collections::singleton)
                .map(ErrorResponse::new)
                .map(internalServerError()::body);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> methodArgumentNotValidExceptionHandler(WebExchangeBindException ex) {
        Collection<ErrorDetails> errorDetails = extractErrors(ex);
        return Mono.just(buildResponseBody(errorDetails));
    }

    private ResponseEntity<ErrorResponse> buildResponseBody(Collection<ErrorDetails> errorDetails) {
        return badRequest()
                .body(new ErrorResponse(errorDetails));
    }

    private List<ErrorDetails> extractErrors(WebExchangeBindException ex) {
        return ex.getFieldErrors()
                .stream()
                .map(error -> new DefaultErrorDetails("Error on field '" + error.getField() + "', " + error.getDefaultMessage()))
                .collect(toList());
    }
}
