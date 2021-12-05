package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiConflictResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiCreatedResponse;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidCreateBookTagRequest;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidUpdateBookTagRequest;
import com.github.sujankumarmitra.libraryservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreateBookTagRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdateBookTagRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.service.BookTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author skmitra
 * @since Nov 30/11/21, 2021
 */

@RestController
@RequestMapping("/api/v1/books/{bookId}/tags")
@AllArgsConstructor
@Tag(
        name = "BookTagController",
        description = "### Controller for managing book tags"
)
public class BookTagController {

    @NonNull
    private final BookTagService bookTagService;

    @Operation(summary = "Create a book tag", description = "Librarians will invoke this api")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = CreateBookTagRequestSchema.class)))
    @ApiCreatedResponse
    @ApiAcceptedResponse
    @ApiConflictResponse
    @PostMapping
    public Mono<ResponseEntity<Object>> createTag(@PathVariable String bookId,
                                                  @RequestBody JacksonValidCreateBookTagRequest request) {

        request.setBookId(bookId);

        return bookTagService
                .createTag(request)
                .map(id -> ResponseEntity.created(URI.create(id)).build())
                .onErrorResume(ApiOperationException.class, err -> Mono.just(ResponseEntity
                        .status(CONFLICT)
                        .body(err.getErrors())));
    }

    @Operation(summary = "Update an existing book tag", description = "Librarians will invoke this api")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = UpdateBookTagRequestSchema.class))
    )
    @ApiConflictResponse
    @ApiBadRequestResponse
    @ApiAcceptedResponse
    @PatchMapping(path = "/{tagId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Object>> updateTag(@PathVariable String bookId,
                                                  @PathVariable String tagId,
                                                  @RequestBody JacksonValidUpdateBookTagRequest request) {

        request.setId(tagId);
        request.setBookId(bookId);

        return bookTagService
                .updateTag(request)
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()))
                .onErrorResume(ApiOperationException.class, err -> Mono.just(ResponseEntity
                        .status(CONFLICT)
                        .body(err.getErrors())));
    }


    @Operation(summary = "Delete a book tag", description = "Librarians will invoke this api")
    @ApiAcceptedResponse
    @DeleteMapping("/{tagId}")
    public Mono<ResponseEntity<Void>> deleteTag(@PathVariable String bookId,
                                                @PathVariable String tagId) {
        return bookTagService
                .deleteTag(tagId)
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()));
    }

}
