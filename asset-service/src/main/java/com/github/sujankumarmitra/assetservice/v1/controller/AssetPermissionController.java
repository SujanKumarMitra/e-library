package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.config.OpenApiConfiguration;
import com.github.sujankumarmitra.assetservice.v1.controller.dto.GrantPermissionRequest;
import com.github.sujankumarmitra.assetservice.v1.exception.AssetNotFoundException;
import com.github.sujankumarmitra.assetservice.v1.service.AssetPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@RestController
@RequestMapping("/api/v1/assets")
@AllArgsConstructor
@Tag(
        name = "AssetPermissionController",
        description = "### Controller for granting clients permission to access assets"
)
@OpenApiConfiguration.ApiSecurityResponse
@OpenApiConfiguration.ApiSecurityScheme
public class AssetPermissionController {

    @NonNull
    private final AssetPermissionService permissionService;

    @Operation(
            description = "# Grant permission to a client to access a asset",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Server has acknowledged the request"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "asset with AssetId not found"
                    )
            }
    )
    @PutMapping("/{assetId}/permissions")
    @PreAuthorize("hasAuthority('WRITE_ASSET')")
    @OpenApiConfiguration.ApiBadRequestResponse
    public Mono<ResponseEntity<Void>> grantPermission(@PathVariable String assetId,
                                                      @RequestBody @Valid GrantPermissionRequest permission) {
        permission.setAssetId(assetId);
        return permissionService
                .grantPermission(permission)
                .map(__ -> ok().build());
    }


    @ExceptionHandler(AssetNotFoundException.class)
    public Mono<ResponseEntity<Void>> assetNotFoundExceptionHandler(AssetNotFoundException ex) {
        return Mono.just(notFound().build());
    }
}