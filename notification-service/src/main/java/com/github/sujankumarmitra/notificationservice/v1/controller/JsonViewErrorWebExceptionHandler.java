package com.github.sujankumarmitra.notificationservice.v1.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.notificationservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.notificationservice.v1.exception.DefaultErrorDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author skmitra
 * @since Sep 30/09/21, 2021
 */
@Component
@Slf4j
@AllArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JsonViewErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper mapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        log.debug("handling:: ", ex);

        ErrorResponse errorResponse = buildErrorResponse(ex);
        ServerHttpResponse httpResponse = exchange.getResponse();

        Mono<DataBuffer> dataBufferMono = Mono.just(errorResponse)
                .handle(this::convertToBytes)
                .map(httpResponse.bufferFactory()::wrap);

        httpResponse.getHeaders().add(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        if (ex instanceof ResponseStatusException)
            httpResponse.setStatusCode(((ResponseStatusException) ex).getStatus());
        else
            httpResponse.setStatusCode(INTERNAL_SERVER_ERROR);
        return httpResponse.writeWith(dataBufferMono);
    }

    private void convertToBytes(ErrorResponse res, SynchronousSink<byte[]> sink) {
        try {
            byte[] bytes = mapper.writeValueAsBytes(res);
            sink.next(bytes);
        } catch (JsonProcessingException e) {
            sink.error(e);
        }
    }

    private ErrorResponse buildErrorResponse(Throwable ex) {
        return new ErrorResponse(List.of(new DefaultErrorDetails(ex.getMessage())));
    }
}
