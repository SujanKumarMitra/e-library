package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreatePackageRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdatePackageRequestSchema;
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
@RequestMapping("/api/v1/packages")
@Tag(
        name = "PackageController",
        description = "### Controller for managing packages"
)
public class PackageController {

    @Operation(
            description = "# Create a package",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            headers = @Header(
                                    name = "Location",
                                    description = "Unique ID pointing to this package",
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
    public Mono<ResponseEntity<Void>> createPackage(@RequestBody CreatePackageRequestSchema request) {
        return Mono.empty();
    }

    @Operation(
            description = "# Update an existing package",
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
    @PatchMapping(path = "/{packageId}",  consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updatePackage(@PathVariable("bookId") String bookId,
                                                    @RequestBody UpdatePackageRequestSchema request) {
        return Mono.empty();
    }

    @Operation(
            description = "# Deletes a package",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            }
    )
    @DeleteMapping("/{packageId}")
    public Mono<ResponseEntity<Void>> deletePackage(@PathVariable("packageId") String packageId) {
        return Mono.empty();
    }
}
