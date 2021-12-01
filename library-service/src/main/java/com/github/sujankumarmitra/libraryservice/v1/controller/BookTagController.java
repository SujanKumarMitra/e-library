package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiConflictResponse;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreateBookTagRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdateBookTagRequestSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 30/11/21, 2021
 */

@RestController
@RequestMapping("/api/v1/books/{bookId}/tags")
@Tag(
        name = "BookTagController",
        description = "### Controller for managing book tags"
)
public class BookTagController {

    @Operation(
            description = "Librarians will invoke this api",
            summary = "Create a book tag"
    )
    @ApiResponse(
            responseCode = "201",
            headers = @Header(
                    name = "Location",
                    description = "Unique ID pointing to this tag",
                    schema = @Schema(
                            example = "7d553b6b-c6e4-42a7-bc8d-7cda07909b2f"
                    )
            )
    )
    @ApiAcceptedResponse
    @ApiConflictResponse
    @PostMapping
    public Mono<ResponseEntity<Void>> createTag(@PathVariable("bookId") String bookId,
                                                @RequestBody CreateBookTagRequestSchema request) {
        return Mono.empty();
    }

    @Operation(
            summary = "Update an existing book tag",
            description = "Librarians will invoke this api")
    @ApiConflictResponse
    @ApiBadRequestResponse
    @ApiAcceptedResponse
    @PatchMapping(path = "/{tagId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updateTag(@PathVariable String bookId,
                                                @PathVariable("tagId") String tagId,
                                                @RequestBody UpdateBookTagRequestSchema request) {
        return Mono.empty();
    }


    @Operation(
            summary = "Delete a book tag",
            description = "Librarians will invoke this api")
    @ApiAcceptedResponse
    @DeleteMapping("/{tagId}")
    public Mono<ResponseEntity<Void>> deleteTag(@PathVariable String bookId,
                                                @PathVariable("tagId") String tagId) {
        return Mono.empty();
    }

}
