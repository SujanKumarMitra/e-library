package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.controller.dto.CreateAssetRequest;
import com.github.sujankumarmitra.assetservice.v1.controller.dto.GetAssetResponse;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.github.sujankumarmitra.assetservice.v1.config.OpenApiConfiguration.*;
import static java.net.URI.create;
import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.created;


/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@RestController
@RequestMapping("/api/assets")
@AllArgsConstructor
@Tag(
        name = "AssetController",
        description = "Controller for creating and deleting assets"
)
@ApiSecurityResponse
@ApiSecurityScheme
public class AssetController {

    @NonNull
    private final AssetService assetService;

    @PostMapping
    @Operation(
            summary = "Create an asset for storing binary object",
            description = "Librarians can invoke this API"
    )
    @ApiCreatedResponse
    @ApiBadRequestResponse
    public Mono<ResponseEntity<Void>> createAsset(Authentication authenticatedUser,
                                                  @RequestBody @Valid CreateAssetRequest request) {

        return assetService
                .createAsset(request)
                .map(Asset::getId)
                .map(assetId -> created(create(assetId)).build());
    }

    @Operation(summary = "Gets assets belonging to a library", description = "Librarians can invoke this API")
    @GetMapping("/{libraryId}")
    public Flux<GetAssetResponse> getAssets(@PathVariable String libraryId) {
        return assetService
                .getAssets(libraryId)
                .map(GetAssetResponse::new);
    }

    @DeleteMapping("/{assetId}")
    @Operation(
            summary = "Deletes an asset.",
            description = "Deletes an existing asset along with the binary object." +
                    "<br>Librarians can invoke this API"
    )
    @ApiAcceptedResponse
    public Mono<ResponseEntity<Void>> deleteAsset(@PathVariable String assetId) {
        return assetService
                .deleteAsset(assetId)
                .thenReturn(assetId)
                .thenReturn(accepted().build());
    }
}
