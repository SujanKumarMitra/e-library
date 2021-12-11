package com.github.sujankumarmitra.ebookprocessor.v1.controller;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookProcessingStatus;
import com.github.sujankumarmitra.ebookprocessor.v1.openapi.schema.GetProcessingStatusResponseSchema;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessingStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@RestController
@RequestMapping("/api/v1/process")
@AllArgsConstructor
@Tag(
        name = "EBookProcessorController",
        description = "Controller for processing ebooks"
)
public class EBookProcessorController {
    @NonNull
    private final EBookProcessingStatusService processingStatusService;

    @Operation(
            summary = "Process a ebook",
            description = "Submit an ebook upload and it will process the ebook and upload it to asset service",
            requestBody = @RequestBody(content = @Content(mediaType = APPLICATION_OCTET_STREAM_VALUE))
    )
    @ApiResponse(
            responseCode = "201",
            description = "Server accepted the request",
            headers = {
                    @Header(name = "Location",
                            description = "Id of a new ebook processing process",
                            schema = @Schema(example = "0ff35627-9086-49e3-9c0d-f9d457f438b2")
                    )
            }
    )
    @ApiResponse(
            responseCode = "409",
            description = "Ebook not found with given id"
    )
    @PutMapping("/{ebookId}")
    public void processEbook(@PathVariable String ebookId, ServerWebExchange exchange) {

    }

    @Operation(
            summary = "Get processing status of an ebook",
            description = "Returns the current state of an ebook processing"
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = GetProcessingStatusResponseSchema.class))
    )
    @GetMapping("/{processId}")
    public Mono<ResponseEntity<EBookProcessingStatus>> getProcessingStatus(@PathVariable String processId) {
        return processingStatusService
                .getStatus(processId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromSupplier(() -> ResponseEntity.notFound().build()));
    }

}
