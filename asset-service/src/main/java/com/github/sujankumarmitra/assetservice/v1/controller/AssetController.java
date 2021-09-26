package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.controller.dto.CreateAssetRequest;
import com.github.sujankumarmitra.assetservice.v1.service.AssetService;
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
public class AssetController {

    @NonNull
    private final AssetService assetService;

    @PostMapping
    public Mono<ResponseEntity<Object>> createAsset(CreateAssetRequest request) {
        return assetService.createAsset(request)
                .map(asset -> created(URI.create(asset.getId())).build())
                .onErrorResume(ControllerUtils::translateErrors);
    }

    @DeleteMapping("/{assetId}")
    public Mono<ResponseEntity<Object>> deleteAsset(@PathVariable String assetId) {
        return assetService
                .deleteAsset(assetId)
                .map(__ -> accepted().build())
                .onErrorResume(ControllerUtils::translateErrors);
    }

}
