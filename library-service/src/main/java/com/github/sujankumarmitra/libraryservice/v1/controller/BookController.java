package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.OpenApiCreateEBookRequest;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.OpenApiCreatePhysicalBookRequest;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.OpenApiUpdateEBookRequest;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.OpenApiUpdatePhysicalBookRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping(path = "/api/v1/books")
@Tag(
        name = "BookController",
        description = "### Controller for managing books"
)
public class BookController {

    @Operation(
            description = "# Create a book",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    oneOf = {OpenApiCreatePhysicalBookRequest.class, OpenApiCreateEBookRequest.class}
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            headers = @Header(
                                    name = "Location",
                                    description = "Unique ID pointing to this book",
                                    schema = @Schema(
                                            example = "7d553b6b-c6e4-42a7-bc8d-7cda07909b2f"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Request body contains errors",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    @PostMapping
    public Mono<ResponseEntity<Void>> createPhysicalBook(@RequestBody OpenApiCreatePhysicalBookRequest request) {
        return Mono.empty();
    }

    @Operation(
            description = "# Update an existing book",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    oneOf = {OpenApiUpdatePhysicalBookRequest.class, OpenApiUpdateEBookRequest.class}
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Request body contains errors",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    @PatchMapping(path = "/{bookId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updatePhysicalBook(@PathVariable("bookId") String bookId,
                                                         @RequestBody OpenApiUpdatePhysicalBookRequest request) {
        return Mono.empty();
    }

    @Operation(
            description = "# Deletes a book",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            }
    )
    @DeleteMapping("/{bookId}")
    public Mono<ResponseEntity<Void>> deletePhysicalBook(@PathVariable("bookId") String bookId) {
        return Mono.empty();
    }
}
