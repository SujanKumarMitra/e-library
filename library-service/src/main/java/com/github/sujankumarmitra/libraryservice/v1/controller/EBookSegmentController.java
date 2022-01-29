package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonGetEBookSegmentResponse;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidCreateEBookSegmentRequest;
import com.github.sujankumarmitra.libraryservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreateEBookSegmentRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.GetEBookSegmentResponseSchema;
import com.github.sujankumarmitra.libraryservice.v1.service.EBookSegmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

import static com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.*;
import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author skmitra
 * @since Dec 02/12/21, 2021
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/ebooks/{ebookId}/segments")
@Tag(
        name = "EBookSegmentController",
        description = "Controller for ebook segments"
)
@ApiSecurityScheme
@ApiSecurityResponse
public class EBookSegmentController {

    @NonNull
    private final EBookSegmentService ebookSegmentService;

    @Operation(
            summary = "Fetch all segments of an ebook",
            description = "Librarians/Teachers/Students will invoke this API"
    )
    @GetMapping
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetEBookSegmentResponseSchema.class)
                    )
            )
    )
    public Flux<JacksonGetEBookSegmentResponse> getAllSegments(@PathVariable String ebookId, @RequestParam(value = "page_no", defaultValue = "0") int pageNo) {
        return ebookSegmentService
                .getSegmentsByEBookId(ebookId, pageNo)
                .map(JacksonGetEBookSegmentResponse::new);
    }

    @Operation(
            summary = "Fetch a segment of an ebook by index",
            description = "Librarians/Teachers/Students will invoke this API"
    )
    @GetMapping("/{segmentIndex}")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GetEBookSegmentResponseSchema.class))
    )
    @ApiNotFoundResponse
    public Mono<ResponseEntity<JacksonGetEBookSegmentResponse>> getSegmentByIndex(
            @PathVariable String ebookId,
            @PathVariable int segmentIndex) {
        return ebookSegmentService
                .getSegmentByBookIdAndIndex(ebookId, segmentIndex)
                .map(JacksonGetEBookSegmentResponse::new)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromSupplier(() -> ResponseEntity.notFound().build()));
    }

    @Operation(
            summary = "Add a segment of an ebook",
            description = "Librarians will invoke this API"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = CreateEBookSegmentRequestSchema.class))
    )
    @PostMapping
    @ApiCreatedResponse
    @ApiBadRequestResponse
    @ApiConflictResponse
    public Mono<ResponseEntity<Object>> createSegment(
            @PathVariable String ebookId,
            @RequestBody @Valid JacksonValidCreateEBookSegmentRequest request) {

        request.setBookId(ebookId);

        return ebookSegmentService
                .createSegment(request)
                .map(id -> ResponseEntity.created(URI.create(id)).build())
                .onErrorResume(ApiOperationException.class,
                        err -> Mono.fromSupplier(() -> ResponseEntity.status(CONFLICT).body(new ErrorResponse(err.getErrors()))));
    }


    @Operation(
            summary = "Delete all segments of an ebook",
            description = "Librarians will invoke this API"
    )
    @DeleteMapping
    @ApiAcceptedResponse
    public Mono<ResponseEntity<Void>> deleteSegments(@PathVariable String bookId) {
        return ebookSegmentService
                .deleteSegmentsByBookId(bookId)
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()));
    }

}
