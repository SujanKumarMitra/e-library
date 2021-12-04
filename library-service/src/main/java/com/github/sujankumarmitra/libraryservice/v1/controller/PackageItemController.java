package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiConflictResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiCreatedResponse;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidCreatePackageItemRequest;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidUpdatePackageItemRequest;
import com.github.sujankumarmitra.libraryservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreatePackageItemRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.UpdatePackageItemRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.service.PackageItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author skmitra
 * @since Nov 30/11/21, 2021
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/packages/{packageId}/items")
@Tag(
        name = "PackageItemController",
        description = "### Controller for managing package items"
)
public class PackageItemController {

    @NonNull
    private final PackageItemService packageItemService;

    @Operation(
            summary = "Creates a package item",
            description = "Librarians/Teachers will invoke this API"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = CreatePackageItemRequestSchema.class))
    )
    @ApiCreatedResponse
    @ApiBadRequestResponse
    @ApiConflictResponse
    @PostMapping
    public Mono<ResponseEntity<Object>> createItem(@PathVariable String packageId,
                                                   @RequestBody @Valid JacksonValidCreatePackageItemRequest request) {

        request.setPackageId(packageId);

        return packageItemService
                .createItem(request)
                .map(id -> ResponseEntity.created(URI.create(id)).build())
                .onErrorResume(ApiOperationException.class,
                        err -> Mono.just(ResponseEntity
                                .status(CONFLICT)
                                .body(err.getErrors())));
    }

    @Operation(summary = "Updates an existing package item",
            description = "Librarians/Teachers will invoke this API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = UpdatePackageItemRequestSchema.class))
    )
    @ApiAcceptedResponse
    @ApiConflictResponse
    @ApiBadRequestResponse
    @PatchMapping(path = "/{itemId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Object>> updateItem(@PathVariable String packageId,
                                                   @PathVariable String itemId,
                                                   @RequestBody @Valid JacksonValidUpdatePackageItemRequest request) {

        request.setId(itemId);
        request.setPackageId(packageId);

        return packageItemService
                .updateItem(request)
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()))
                .onErrorResume(ApiOperationException.class,
                        err -> Mono.just(ResponseEntity
                                .status(CONFLICT)
                                .body(err.getErrors())));
    }


    @Operation(summary = "Deletes a package item",
            description = "Librarians/Teachers will invoke this API to delete a package item")
    @ApiAcceptedResponse
    @DeleteMapping("/{itemId}")
    public Mono<ResponseEntity<Void>> deleteItem(@PathVariable String packageId,
                                                 @PathVariable String itemId) {
        return packageItemService
                .deleteItem(itemId)
                .thenReturn(ResponseEntity.accepted().build());
    }

}
