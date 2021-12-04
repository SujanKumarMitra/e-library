package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiConflictResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiCreatedResponse;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreatePackageTagRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdatePackageTagRequestSchema;
import io.swagger.v3.oas.annotations.Operation;
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
            summary = "Create a package tag",
            description = "Librarians/Teachers will invoke this API"
    )
    @ApiCreatedResponse
    @ApiAcceptedResponse
    @ApiBadRequestResponse
    @ApiConflictResponse
    @ApiResponse(
            responseCode = "409",
            description = "Tag with key already exists"
    )
    @PostMapping
    public Mono<ResponseEntity<Void>> createTag(@PathVariable("packageId") String packageId,
                                                @RequestBody CreatePackageTagRequestSchema request) {
        return Mono.empty();
    }

    @Operation(
            summary = "Update an existing package tag",
            description = "Librarians/Teachers will invoke this API"
    )
    @ApiConflictResponse
    @ApiAcceptedResponse
    @PatchMapping(path = "/{tagId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updateTag(@PathVariable("packageId") String packageId,
                                                @PathVariable("tagId") String tagId,
                                                @RequestBody UpdatePackageTagRequestSchema request) {
        return Mono.empty();
    }


    @Operation(
            summary = "Delete a package tag",
            description = "Librarians/Teachers will invoke this API"
    )
    @ApiAcceptedResponse
    @DeleteMapping("/{tagId}")
    public Mono<ResponseEntity<Void>> deleteTag(@PathVariable("packageId") String packageId,
                                                @PathVariable("tagId") String tagId) {
        return Mono.empty();
    }

}
