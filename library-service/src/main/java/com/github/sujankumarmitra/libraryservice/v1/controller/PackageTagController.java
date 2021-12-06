package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiConflictResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiCreatedResponse;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidCreatePackageTagRequest;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidUpdatePackageTagRequest;
import com.github.sujankumarmitra.libraryservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreatePackageTagRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdatePackageTagRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.service.PackageTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author skmitra
 * @since Nov 30/11/21, 2021
 */

@RestController
@RequestMapping("/api/v1/packages/{packageId}/tags")
@AllArgsConstructor
@Tag(
        name = "PackageTagController",
        description = "Controller for managing package tags"
)
public class PackageTagController {

    @NonNull
    private final PackageTagService packageTagService;

    @Operation(summary = "Create a package tag", description = "Librarians/Teachers will invoke this API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = CreatePackageTagRequestSchema.class))
    )
    @ApiCreatedResponse
    @ApiAcceptedResponse
    @ApiBadRequestResponse
    @ApiConflictResponse
    @PostMapping
    public Mono<ResponseEntity<Object>> createTag(@PathVariable String packageId,
                                                  @RequestBody JacksonValidCreatePackageTagRequest request) {

        request.setPackageId(packageId);
        return packageTagService
                .createTag(request)
                .map(id -> ResponseEntity.created(URI.create(id)).build())
                .onErrorResume(ApiOperationException.class, err -> Mono.just(ResponseEntity
                        .status(CONFLICT)
                        .body(err.getErrors())));

    }

    @Operation(summary = "Update an existing package tag", description = "Librarians/Teachers will invoke this API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = UpdatePackageTagRequestSchema.class))
    )
    @ApiConflictResponse
    @ApiAcceptedResponse
    @PatchMapping(path = "/{tagId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Object>> updateTag(@PathVariable("packageId") String packageId,
                                                  @PathVariable("tagId") String tagId,
                                                  @RequestBody JacksonValidUpdatePackageTagRequest request) {

        request.setId(tagId);
        request.setPackageId(packageId);

        return packageTagService
                .updateTag(request)
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()))
                .onErrorResume(ApiOperationException.class, err -> Mono.just(ResponseEntity
                        .status(CONFLICT)
                        .body(err.getErrors())));
    }


    @Operation(summary = "Delete a package tag", description = "Librarians/Teachers will invoke this API")
    @ApiAcceptedResponse
    @DeleteMapping("/{tagId}")
    public Mono<ResponseEntity<Void>> deleteTag(@PathVariable String packageId,
                                                @PathVariable String tagId) {
        return packageTagService
                .deleteTag(tagId)
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()));
    }

}
