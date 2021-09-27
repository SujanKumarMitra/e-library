package com.github.sujankumarmitra.assetservice.v1;

import com.github.sujankumarmitra.assetservice.v1.dao.ErrorResponse;
import com.github.sujankumarmitra.assetservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.assetservice.v1.exception.DefaultErrorDetails;
import com.github.sujankumarmitra.assetservice.v1.exception.ErrorDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@RestControllerAdvice
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
}
