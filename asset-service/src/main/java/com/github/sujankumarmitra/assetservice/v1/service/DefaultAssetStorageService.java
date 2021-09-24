package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.config.AssetStorageConfiguration;
import com.github.sujankumarmitra.assetservice.v1.dto.DefaultStoredAsset;
import com.github.sujankumarmitra.assetservice.v1.dto.StoredAsset;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
//@Service
@AllArgsConstructor
public class DefaultAssetStorageService implements AssetStorageService {

    private final AssetService assetService;
    private final AssetStorageConfiguration storageConfiguration;


    @Override
    public Mono<Void> storeObject(String assetId, Flux<DataBuffer> dataBuffers) {
        String baseDir = storageConfiguration.getBaseDir();
        Path writePath = Path.of(baseDir, assetId);

        return assetService.getAsset(assetId)
                .flatMap(asset -> writeToDisk(writePath, dataBuffers));
    }

    @Override
    public Mono<StoredAsset> retrieveObject(String assetId) {
        Mono<Asset> asset = assetService.getAsset(assetId);
        Mono<InputStreamSource> inputStreamSource = asset.map(this::fetchFromDisk);

        return Mono.zip(asset, inputStreamSource, DefaultStoredAsset::new);
    }

    @Override
    public Mono<Void> purgeObject(String assetId) {
        String baseDir = storageConfiguration.getBaseDir();
        Path deletePath = Path.of(baseDir, assetId);

        return Mono.create(sink -> deleteFile(deletePath, sink));
    }

    private void deleteFile(Path deletePath, MonoSink<Void> sink) {
        try {
            Files.delete(deletePath);
        } catch (NoSuchFileException e) {
            // ignore
        } catch (IOException e) {
            sink.error(e);
        }
        sink.success();
    }

    private InputStreamSource fetchFromDisk(Asset asset) {
        String baseDir = storageConfiguration.getBaseDir();
        Path readPath = Path.of(baseDir, asset.getId());

        return new FileSystemResource(readPath);
    }

    private Mono<Void> writeToDisk(Path writePath, Flux<DataBuffer> dataBuffers) {
        return DataBufferUtils.write(
                dataBuffers,
                writePath,
                WRITE,
                CREATE);
    }


}
