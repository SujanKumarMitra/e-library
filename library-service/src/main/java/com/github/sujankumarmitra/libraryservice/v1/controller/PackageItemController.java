package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.OpenApiCreatePackageItemRequest;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.OpenApiUpdatePackageItem;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.OpenApiUpdatePackageItemRequest;
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
@RequestMapping("/api/v1/packages/{packageId}/items")
@Tag(
        name = "PackageItemController",
        description = "### Controller for managing package items"
)
public class PackageItemController {

    @Operation(
            description = "# Creates a package item",
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
    public Mono<ResponseEntity<Void>> createItem(@PathVariable("packageId") String packageId,
                                                 @RequestBody OpenApiCreatePackageItemRequest request) {
        return Mono.empty();
    }

    @Operation(
            description = "# Updates an existing package item",
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
    @PatchMapping(path = "/{itemId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updateItem(@PathVariable("itemId") String itemId,
                                                @RequestBody OpenApiUpdatePackageItemRequest request) {
        return Mono.empty();
    }


    @Operation(
            description = "# Deletes a package tag",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            }
    )
    @DeleteMapping("/{itemId}")
    public Mono<ResponseEntity<Void>> deleteItem(@PathVariable("itemId") String itemId) {
        return Mono.empty();
    }

}
