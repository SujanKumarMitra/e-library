package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.dto.StoredAsset;
import com.github.sujankumarmitra.assetservice.v1.service.AssetStorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class AssetStorageController {

    public static final String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"";
    private final AssetStorageService assetStorageService;

    @PostMapping(value = "/upload/{assetId}")
    public Mono<ResponseEntity<Void>> uploadAsset(String assetId, ServerWebExchange exchange) {
        Flux<DataBuffer> dataBuffers = Mono.fromCallable(exchange::getMultipartData)
                .flatMap(Function.identity())
                .map(map -> map.getFirst("file"))
                .flatMapMany(Part::content);

        return assetStorageService
                .storeObject(assetId, dataBuffers)
                .map(ResponseEntity::ok);

    }

    @GetMapping("/download/{assetId}")
    public Mono<ResponseEntity<InputStreamSource>> getObject(String assetId) {
        return assetStorageService.retrieveObject(assetId)
                .map(this::toResponseEntity);
    }

    private ResponseEntity<InputStreamSource> toResponseEntity(StoredAsset storedAsset) {
        String assetName = storedAsset.getAsset().getName();
        InputStreamSource streamSource = storedAsset.getInputStreamSource();
        return ResponseEntity
                .ok()
                .header(CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_FORMAT, assetName))
                .body(streamSource);
    }
}
