package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.config.OpenApiConfiguration;
import com.github.sujankumarmitra.assetservice.v1.controller.dto.CreateAssetRequest;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import com.github.sujankumarmitra.assetservice.v1.model.DefaultAssetPermission;
import com.github.sujankumarmitra.assetservice.v1.service.AssetPermissionService;
import com.github.sujankumarmitra.assetservice.v1.service.AssetService;
import com.github.sujankumarmitra.assetservice.v1.service.AssetStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.github.sujankumarmitra.assetservice.v1.model.AssetPermission.INFINITE_GRANT_DURATION;
import static java.lang.System.currentTimeMillis;
import static java.net.URI.create;
import static org.springframework.http.ResponseEntity.*;
import static reactor.core.publisher.Mono.just;


/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@RestController
@RequestMapping("/api/v1/assets")
@AllArgsConstructor
@Tag(
        name = "AssetController",
        description = "### Controller for creating and deleting assets"
)
@OpenApiConfiguration.ApiSecurityResponse
public class AssetController {

    @NonNull
    private final AssetService assetService;
    @NonNull
    private final AssetPermissionService assetPermissionService;
    @NonNull
    private final AssetStorageService assetStorageService;

    @PostMapping
    @Operation(
            description = "# Create an asset for storing binary object",
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            headers = @Header(
                                    name = "Location",
                                    description = "Unique ID pointing to this asset",
                                    schema = @Schema(
                                            example = "/7d553b6b-c6e4-42a7-bc8d-7cda07909b2f"
                                    )
                            )

                    )
            }
    )
    @PreAuthorize("hasAuthority('WRITE_ASSET')")
    public Mono<ResponseEntity<Void>> createAsset(Authentication authenticatedUser,
                                                    @RequestBody @Schema(description = "Schema for creating a new Asset") CreateAssetRequest request) {

        return assetService
                .createAsset(request)
                .map(Asset::getId)
                .zipWith(just(authenticatedUser.getName()), this::getAssetPermission)
                .flatMap(this::grantPermissionToAssetCreator)
                .map(assetId -> created(create(assetId)).build());
    }

    @DeleteMapping("/{assetId}")
    @Operation(
            description = "# Deletes an existing asset. \n\n" +
                    "**Please note that the associated binary object will also get deleted**",
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            description = "Request is acknowledged by the server"
                    )
            }
    )
    @PreAuthorize("hasAuthority('WRITE_ASSET')")
    public Mono<ResponseEntity<Void>> deleteAsset(@PathVariable String assetId) {
        return assetService
                .deleteAsset(assetId)
                .thenReturn(accepted().build());
    }



    private Mono<String> grantPermissionToAssetCreator(AssetPermission assetPermission) {
        return assetPermissionService
                .grantPermission(assetPermission)
                .thenReturn(assetPermission.getAssetId());
    }

    private AssetPermission getAssetPermission(String assetId, String subjectId) {
        return DefaultAssetPermission
                .newBuilder()
                .assetId(assetId)
                .subjectId(subjectId)
                .grantStartEpochMilliseconds(currentTimeMillis())
                .grantDurationInMilliseconds(INFINITE_GRANT_DURATION)
                .build();
    }
}
