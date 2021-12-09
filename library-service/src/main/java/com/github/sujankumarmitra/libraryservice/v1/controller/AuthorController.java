package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidCreateAuthorRequest;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidUpdateAuthorRequest;
import com.github.sujankumarmitra.libraryservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreateAuthorRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdateBookAuthorRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.security.SecurityAnnotations.RoleLibrarian;
import com.github.sujankumarmitra.libraryservice.v1.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

import static com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.*;

/**
 * @author skmitra
 * @since Nov 30/11/21, 2021
 */

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/books/{bookId}/authors")
@Tag(
        name = "AuthorController",
        description = "Controller for managing book authors"
)
@ApiSecurityScheme
@ApiSecurityResponse
public class AuthorController {

    @NonNull
    private final AuthorService authorService;

    @Operation(
            summary = "Create an author",
            description = "Librarians will invoke this API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = CreateAuthorRequestSchema.class))
    )
    @ApiCreatedResponse
    @ApiBadRequestResponse
    @ApiConflictResponse
    @PostMapping
    @RoleLibrarian
    public Mono<ResponseEntity<Object>> createAuthor(@PathVariable("bookId") String bookId,
                                                     @RequestBody @Valid JacksonValidCreateAuthorRequest request) {

        request.setBookId(bookId);
        return authorService
                .createAuthor(request)
                .map(id -> ResponseEntity.created(URI.create(id)).build())
                .onErrorResume(ApiOperationException.class, err -> Mono.just(ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(err.getErrors())));

    }

    @Operation(
            summary = "Update an existing author",
            description = "Librarians will invoke this API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = UpdateBookAuthorRequestSchema.class))
    )
    @ApiConflictResponse
    @ApiAcceptedResponse
    @RoleLibrarian
    @PatchMapping(path = "/{authorId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Object>> updateAuthor(@PathVariable String bookId,
                                                     @PathVariable String authorId,
                                                     @RequestBody @Valid JacksonValidUpdateAuthorRequest request) {

        request.setBookId(bookId);
        request.setId(authorId);

        return authorService
                .updateAuthor(request)
                .thenReturn(ResponseEntity.accepted().build())
                .onErrorResume(ApiOperationException.class, err -> Mono.just(ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(err.getErrors())));
    }


    @Operation(
            summary = "Delete an author",
            description = "Librarians will invoke this API")
    @ApiAcceptedResponse
    @RoleLibrarian
    @DeleteMapping("/{authorId}")
    public Mono<ResponseEntity<Void>> deleteAuthor(
            @PathVariable String bookId,
            @PathVariable String authorId) {
        return authorService
                .deleteAuthor(authorId)
                .thenReturn(ResponseEntity.accepted().build());
    }


}
