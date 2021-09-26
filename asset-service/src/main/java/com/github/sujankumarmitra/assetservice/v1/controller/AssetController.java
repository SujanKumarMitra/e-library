package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.controller.dto.CreateAssetRequest;
import com.github.sujankumarmitra.assetservice.v1.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.created;


/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@RestController
@RequestMapping("/v1/asset")
@AllArgsConstructor
@Tag(
        name = "AssetController",
        description = "### Controller for creating and deleting assets"
)
public class AssetController {

    @NonNull
    private final AssetService assetService;

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
    public Mono<ResponseEntity<Object>> createAsset(@RequestBody @Schema(description = "Schema for creating a new Asset") CreateAssetRequest request) {
        return assetService.createAsset(request)
                .map(asset -> created(URI.create(asset.getId())).build())
                .onErrorResume(ControllerUtils::translateErrors);
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
    public Mono<ResponseEntity<Object>> deleteAsset(@PathVariable String assetId) {
        return assetService
                .deleteAsset(assetId)
                .map(__ -> accepted().build())
                .onErrorResume(ControllerUtils::translateErrors);
    }

}
