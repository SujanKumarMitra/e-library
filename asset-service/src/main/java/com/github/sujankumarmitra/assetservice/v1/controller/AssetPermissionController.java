package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.controller.dto.GrantPermissionRequest;
import com.github.sujankumarmitra.assetservice.v1.exception.AssetNotFoundException;
import com.github.sujankumarmitra.assetservice.v1.service.AssetPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.github.sujankumarmitra.assetservice.v1.config.OpenApiConfiguration.*;
import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.notFound;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@RestController
@RequestMapping("/api/assets")
@AllArgsConstructor
@Tag(
        name = "AssetPermissionController",
        description = "Controller for granting access permission to clients"
)
@ApiSecurityResponse
@ApiSecurityScheme
public class AssetPermissionController {

    @NonNull
    private final AssetPermissionService permissionService;

    @Operation(
            summary = "Grant permission to a client to access a asset",
            description = "Scopes required=WRITE_ASSET"
    )
    @ApiAcceptedResponse
    @ApiNotFoundResponse
    @ApiBadRequestResponse
    @PatchMapping("/{assetId}/permissions")
    public Mono<ResponseEntity<Void>> grantPermission(@PathVariable String assetId,
                                                      @RequestBody @Valid GrantPermissionRequest permission) {
        permission.setAssetId(assetId);
        return permissionService
                .grantPermission(permission)
                .thenReturn(accepted().build());
    }


    @ExceptionHandler(AssetNotFoundException.class)
    public Mono<ResponseEntity<Void>> assetNotFoundExceptionHandler(AssetNotFoundException ex) {
        return Mono.just(notFound().build());
    }
}