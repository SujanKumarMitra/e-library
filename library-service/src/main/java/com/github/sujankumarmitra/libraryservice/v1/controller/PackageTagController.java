package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreatePackageTagRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdateBookTagRequestSchema;
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
@RequestMapping("/api/v1/packages/{packageId}/tags")
@Tag(
        name = "PackageTagController",
        description = "### Controller for managing package tags"
)
public class PackageTagController {


    @Operation(
            description = "# Create a package tag",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            headers = @Header(
                                    name = "Location",
                                    description = "Unique ID pointing to this tag",
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
    public Mono<ResponseEntity<Void>> createTag(@PathVariable("bookId") String bookId,
                                               @RequestBody CreatePackageTagRequestSchema request) {
        return Mono.empty();
    }

    @Operation(
            description = "# Update an existing book tag",
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
    @PatchMapping(path = "/{tagId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updateTag(@PathVariable("tagId") String tagId,
                                                @RequestBody UpdateBookTagRequestSchema request) {
        return Mono.empty();
    }


    @Operation(
            description = "# Delete a package tag",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            }
    )
    @DeleteMapping("/{tagId}")
    public Mono<ResponseEntity<Void>> deleteTag(@PathVariable("tagId") String tagId) {
        return Mono.empty();
    }

}
