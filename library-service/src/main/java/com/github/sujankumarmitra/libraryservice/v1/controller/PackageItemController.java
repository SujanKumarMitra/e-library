package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiConflictResponse;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreatePackageItemRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdatePackageItemRequestSchema;
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
@RequestMapping("/api/v1/packages/{packageId}/items")
@Tag(
        name = "PackageItemController",
        description = "### Controller for managing package items"
)
public class PackageItemController {

    @Operation(
            summary = "Creates a package item",
            description = "Librarians/Teachers will invoke this API"
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
    @ApiBadRequestResponse
    @ApiConflictResponse
    @PostMapping
    public Mono<ResponseEntity<Void>> createItem(@PathVariable("packageId") String packageId,
                                                 @RequestBody CreatePackageItemRequestSchema request) {
        return Mono.empty();
    }

    @Operation(summary = "Updates an existing package item",
            description = "Librarians/Teachers will invoke this API")
    @ApiAcceptedResponse
    @ApiConflictResponse
    @ApiBadRequestResponse
    @PatchMapping(path = "/{itemId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updateItem(@PathVariable("packageId") String packageId,
                                                 @PathVariable("itemId") String itemId,
                                                 @RequestBody UpdatePackageItemRequestSchema request) {
        return Mono.empty();
    }


    @Operation(summary = "Deletes a package item",
            description = "Librarians/Teachers will invoke this API to delete a package item")
    @ApiAcceptedResponse
    @DeleteMapping("/{itemId}")
    public Mono<ResponseEntity<Void>> deleteItem(@PathVariable("packageId") String packageId,
                                                 @PathVariable("itemId") String itemId) {
        return Mono.empty();
    }

}
