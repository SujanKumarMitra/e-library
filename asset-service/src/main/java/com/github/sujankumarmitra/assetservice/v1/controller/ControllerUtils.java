package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.assetservice.v1.exception.DefaultErrorDetails;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;
import static reactor.core.publisher.Mono.just;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
class ControllerUtils {

    private ControllerUtils() {
    }


    public static Mono<ResponseEntity<Object>> translateErrors(Throwable th) {
        if (th instanceof ApiOperationException)
            return just(
                    badRequest()
                            .body(((ApiOperationException) th).getErrors()));
        else
            return just(
                    internalServerError()
                            .body(List.of(new DefaultErrorDetails(th.getMessage()))));
    }
}
