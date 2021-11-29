package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.OpenApiCreateEBookRequest;
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
 * @since Nov 29/11/21, 2021
 */
@RestController
@RequestMapping("/api/v1/ebooks")
@Tag(
        name = "EBookController",
        description = "### Controller for managing ebooks"
)
public class EBookController {


    @Operation(
            description = "# Create an ebook",
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
    public Mono<ResponseEntity<Void>> createPhysicalBook(@RequestBody OpenApiCreateEBookRequest request) {
        return Mono.empty();
    }

    @Operation(
            description = "# Update an existing ebook",
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
    @PatchMapping("/{bookId}")
    public Mono<ResponseEntity<Void>> updatePhysicalBook(@PathVariable("bookId") String bookId,
                                                         @RequestBody OpenApiUpdatePhysicalBookRequest request) {
        return Mono.empty();
    }

    @Operation(
            description = "# Delete a ebook",
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
