package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiCreatedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiNotFoundResponse;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.*;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreatePackageRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.GetPackageResponseSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdatePackageRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;

import static com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiSecurityResponse;
import static com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiSecurityScheme;

/**
 * @author skmitra
 * @since Nov 29/11/21, 2021
 */
@RestController
@RequestMapping("/api/packages")
@AllArgsConstructor
@Tag(
        name = "PackageController",
        description = "Controller for managing packages"
)
@ApiSecurityScheme
@ApiSecurityResponse
public class PackageController {

    @NonNull
    private final PackageService packageService;

    @Operation(
            summary = "Fetch packages",
            description = "Librarians/Teachers/Students can invoke this API to view packages")
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
    public Flux<JacksonGetPackageResponse> getPackages(
            @RequestParam("library_id") String libraryId,
            @RequestParam(value = "page_no", defaultValue = "0") int pageNo) {
        return packageService
                .getPackages(libraryId, pageNo)
                .map(JacksonGetPackageResponse::new);
    }

    @Operation(summary = "Fetch package by id", description = "Students/Teachers/Librarians can invoke this api")
    @ApiResponse(content = @Content(schema = @Schema(implementation = GetPackageResponseSchema.class)))
    @GetMapping("/{packageId}")
    @ApiNotFoundResponse
    public Mono<ResponseEntity<JacksonGetPackageResponse>> getPackage(
            @PathVariable String packageId) {
        return packageService
                .getPackage(packageId)
                .map(JacksonGetPackageResponse::new)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromSupplier(() -> ResponseEntity.notFound().build()));
    }


    @Operation(
            summary = "Fetch packages by name starting with",
            description = "Librarians/Teachers/Students can invoke this API to search by package name")
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
    public Flux<JacksonGetPackageResponse> getPackagesByNameStartingWith(
            @RequestParam("library_id") String libraryId,
            @RequestParam(value = "name_prefix") String namePrefix,
            @RequestParam(value = "page_no", defaultValue = "0") int pageNo) {
        return packageService
                .getPackagesByName(libraryId, namePrefix, pageNo)
                .map(JacksonGetPackageResponse::new);
    }


    @Operation(summary = "Create a package",
            description = "Librarians/Teachers can invoke this API to create a package")

    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = CreatePackageRequestSchema.class))
    )
    @ApiCreatedResponse
    @ApiBadRequestResponse
    @PostMapping
    public Mono<ResponseEntity<Void>> createPackage(@RequestBody @Valid JacksonValidCreatePackageRequest request) {
        return packageService
                .createPackage(request)
                .map(id -> ResponseEntity.created(URI.create(id)).build());
    }

    @Operation(summary = "Update an existing package",
            description = "Librarians/Teachers can invoke this API to update a package")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    schema = @Schema(implementation = UpdatePackageRequestSchema.class)
            )
    )
    @ApiAcceptedResponse
    @ApiBadRequestResponse
    @PatchMapping(path = "/{packageId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updatePackage(@PathVariable("packageId") String packageId,
                                                    @RequestBody JacksonValidUpdatePackageRequest request) {

        request.setId(packageId);
        Set<JacksonValidUpdatePackageItemRequest> items = request.getItems();
        Set<JacksonValidUpdatePackageTagRequest> tags = request.getTags();

        if (items != null) items.forEach(item -> item.setPackageId(packageId));
        if (tags != null) tags.forEach(tag -> tag.setPackageId(packageId));

        return packageService
                .updatePackage(request)
                .thenReturn(ResponseEntity.accepted().build());
    }

    @Operation(summary = " Deletes a package",
            description = "Librarians/Teachers can invoke this API to delete a package")
    @ApiAcceptedResponse
    @DeleteMapping("/{packageId}")
    public Mono<ResponseEntity<Void>> deletePackage(@PathVariable String packageId) {

        return packageService
                .deletePackage(packageId)
                .thenReturn(ResponseEntity.accepted().build());
    }
}
