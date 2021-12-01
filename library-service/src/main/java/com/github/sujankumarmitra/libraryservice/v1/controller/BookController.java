package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
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
            summary = "Fetch all books",
            description = "Librarians/Teachers/Students will invoke this API." +
                    "<br> Please refer to the response schema to see how book type is determined",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    oneOf = {GetPhysicalBookResponseSchema.class, GetEBookResponseSchema.class}
                                            )
                                    )
                            )
                    )
            }
    )
    @GetMapping
    public Flux<Book> getBooks(@RequestParam(value = "page_no", defaultValue = "0") long pageNo) {
        return Flux.empty();
    }


    @Operation(
            summary = "Search books by title and author",
            description = "Librarians/Teachers/Students will invoke this API." +
                    "<br> Please refer to the response schema to see how book type is determined",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    oneOf = {GetPhysicalBookResponseSchema.class, GetEBookResponseSchema.class}
                                            )
                                    )
                            )
                    )
            }
    )
    @GetMapping("/search")
    public Flux<Book> getBooksByTitle(
            @RequestParam(required = false) String title,
            @RequestParam(value = "page_no", defaultValue = "0") long pageNo) {
        return Flux.empty();
    }


    @Operation(
            summary = "Create a book",
            description = "Librarians will invoke this API." +
                    "<br> Please refer to the request schema for separate book type",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    oneOf = {CreatePhysicalBookRequestSchema.class, CreateEBookRequestSchema.class}
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
                    )
            }
    )
    @ApiBadRequestResponse
    @ApiAcceptedResponse
    @PostMapping
    public Mono<ResponseEntity<Void>> createBook(ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Operation(
            summary = "Update an existing book",
            description = "Librarians will invoke this API." +
                    "<br> Please refer to the request schema for separate book type"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    schema = @Schema(
                            oneOf = {UpdatePhysicalBookRequestSchema.class, UpdateEBookRequestSchema.class}
                    )
            )
    )
    @ApiResponse(responseCode = "202")
    @ApiAcceptedResponse
    @ApiBadRequestResponse
    @PatchMapping(path = "/{bookId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updateBook(@PathVariable("bookId") String bookId,
                                                 ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Operation(
            summary = "Delete a book",
            description = "Librarians will invoke this API")
    @ApiAcceptedResponse
    @DeleteMapping("/{bookId}")
    public Mono<ResponseEntity<Void>> deleteBook(@PathVariable("bookId") String bookId) {
        return Mono.empty();
    }
}
