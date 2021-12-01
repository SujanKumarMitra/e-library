package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreatePackageRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.GetPackageResponseSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdatePackageRequestSchema;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@RestController
@RequestMapping("/api/v1/packages")
@Tag(
        name = "PackageController",
        description = "### Controller for managing packages"
)
public class PackageController {

    @Operation(
            summary = "Fetch packages",
            description = "Librarians/Teachers/Students will invoke this API to view packages")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetPackageResponseSchema.class)
                    )
            )
    )
    @GetMapping
    public Flux<Package> getPackages(@RequestParam(value = "page_no", defaultValue = "0") long pageNo) {
        return Flux.empty();
    }


    @Operation(
            summary = "Fetch packages by name starting with",
            description = "Librarians/Teachers/Students will invoke this API to search by package name")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetPackageResponseSchema.class)
                    )
            )
    )
    @GetMapping("/search")
    public Flux<Package> getPackagesByNameStartingWith(@RequestParam(value = "name_starting_with") String nameStartingWith,
                                                       @RequestParam(value = "page_no", defaultValue = "0") long pageNo) {
        return Flux.empty();
    }


    @Operation(summary = "Create a package",
            description = "Librarians/Teachers will invoke this API to create a package")
    @ApiResponse(responseCode = "201",
            headers = @Header(
                    name = "Location",
                    description = "Unique ID pointing to this package",
                    schema = @Schema(
                            example = "7d553b6b-c6e4-42a7-bc8d-7cda07909b2f"
                    )
            )
    )
    @ApiBadRequestResponse
    @PostMapping
    public Mono<ResponseEntity<Void>> createPackage(@RequestBody CreatePackageRequestSchema request) {
        return Mono.empty();
    }

    @Operation(summary = "Update an existing package",
            description = "Librarians/Teachers will invoke this API to update a package")
    @ApiAcceptedResponse
    @ApiBadRequestResponse
    @PatchMapping(path = "/{packageId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updatePackage(@PathVariable("packageId") String packageId,
                                                    @RequestBody UpdatePackageRequestSchema request) {
        return Mono.empty();
    }

    @Operation(summary = " Deletes a package",
            description = "Librarians/Teachers will invoke this API to delete a package")
    @ApiAcceptedResponse
    @DeleteMapping("/{packageId}")
    public Mono<ResponseEntity<Void>> deletePackage(@PathVariable("packageId") String packageId) {
        return Mono.empty();
    }
}
