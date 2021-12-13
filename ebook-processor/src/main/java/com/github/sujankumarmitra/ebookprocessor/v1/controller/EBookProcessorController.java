package com.github.sujankumarmitra.ebookprocessor.v1.controller;

import com.github.sujankumarmitra.ebookprocessor.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.ebookprocessor.v1.exception.EBookNotFoundException;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookProcessRequest;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookProcessingStatus;
import com.github.sujankumarmitra.ebookprocessor.v1.model.impl.DefaultEBookProcessRequest;
import com.github.sujankumarmitra.ebookprocessor.v1.openapi.schema.GetProcessingStatusResponseSchema;
import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationToken;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessingService;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessingStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.github.sujankumarmitra.ebookprocessor.v1.config.OpenApiConfiguration.*;
import static com.github.sujankumarmitra.ebookprocessor.v1.security.SecurityAnnotations.*;
import static com.github.sujankumarmitra.ebookprocessor.v1.security.SecurityAnnotations.EBookProcessStatusScope;
import static org.springframework.http.HttpStatus.CONFLICT;
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
@ApiSecurityScheme
@ApiSecurityResponse
public class EBookProcessorController {
    @NonNull
    private final EBookProcessingStatusService processingStatusService;
    @NonNull
    private final EBookProcessingService processingService;

    @Operation(
            summary = "Process a ebook",
            description = "Submit an ebook upload and it will process the ebook " +
                    "and upload it to <a href=\"https://github.com/SujanKumarMitra/e-library/tree/main/asset-service\">asset-service</a>." +
                    "<br> Librarians with 'WRITE_ASSET' scope can invoke this API."
    )
    @RequestBody(
            description = "a stream of bytes",
            required = true,
            content = {@Content(
                    mediaType = APPLICATION_OCTET_STREAM_VALUE,
                    schema = @Schema(
                            description = "a stream of bytes",
                            implementation = byte[].class,
                            format = "binary"
                    )
            )

            }
    )
    @ApiCreatedResponse
    @ApiConflictResponse
    @ProcessEBookScope
    @PutMapping("/{ebookId}")
    public Mono<ResponseEntity<Object>> processEbook(@PathVariable String ebookId,
                                                     ServerHttpRequest request,
                                                     Authentication authentication) {
        EBookProcessRequest processRequest = new DefaultEBookProcessRequest(
                ebookId,
                (AuthenticationToken) authentication,
                request.getBody());

        return processingService
                .submitProcess(processRequest)
                .map(id -> ResponseEntity.created(URI.create(id)).build())
                .onErrorResume(EBookNotFoundException.class,
                        err -> Mono.fromSupplier(() ->
                                ResponseEntity.status(CONFLICT).body(new ErrorResponse(err.getErrors()))));
    }

    @Operation(
            summary = "Get processing status of an ebook",
            description = "Returns the current status of an ebook processing." +
                    "<br> Librarians can invoke this API."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = GetProcessingStatusResponseSchema.class))
    )
    @GetMapping("/{processId}")
    @EBookProcessStatusScope
    @ApiNotFoundResponse
    public Mono<ResponseEntity<EBookProcessingStatus>> getProcessingStatus(@PathVariable String processId) {
        return processingStatusService
                .getStatus(processId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromSupplier(() -> ResponseEntity.notFound().build()));
    }

}
