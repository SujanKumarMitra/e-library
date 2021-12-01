package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiConflictResponse;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreateAuthorRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdateBookAuthorRequestSchema;
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
@RequestMapping("/api/v1/books/{bookId}/authors")
@Tag(
        name = "AuthorController",
        description = "### Controller for managing book authors"
)
public class AuthorController {


    @Operation(
            summary = "Create an author",
            description = "Librarians will invoke this API")
    @ApiResponse(
            responseCode = "201",
            headers = @Header(
                    name = "Location",
                    description = "Unique ID pointing to this author",
                    schema = @Schema(
                            example = "7d553b6b-c6e4-42a7-bc8d-7cda07909b2f"
                    )
            )
    )
    @ApiBadRequestResponse
    @ApiConflictResponse
    @PostMapping
    public Mono<ResponseEntity<Void>> createAuthor(@PathVariable("bookId") String bookId,
                                                   @RequestBody CreateAuthorRequestSchema request) {
        return Mono.empty();
    }

    @Operation(
            summary = "Update an existing author",
            description = "Librarians will invoke this API")
    @ApiConflictResponse
    @ApiAcceptedResponse
    @PatchMapping(path = "/{authorId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updateAuthor(@PathVariable String bookId,
                                                   @PathVariable("authorId") String authorId,
                                                   @RequestBody UpdateBookAuthorRequestSchema request) {
        return Mono.empty();
    }


    @Operation(
            summary = "Delete an author",
            description = "Librarians will invoke this API")
    @ApiAcceptedResponse
    @DeleteMapping("/{authorId}")
    public Mono<ResponseEntity<Void>> deleteAuthor(
            @PathVariable String bookId,
            @PathVariable("authorId") String authorId) {
        return Mono.empty();
    }


}
