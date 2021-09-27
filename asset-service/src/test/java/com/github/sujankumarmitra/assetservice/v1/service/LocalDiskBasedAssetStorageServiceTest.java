package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.config.AssetStorageProperties;
import com.github.sujankumarmitra.assetservice.v1.exception.AssetNotFoundException;
import com.github.sujankumarmitra.assetservice.v1.model.DefaultAsset;
import com.github.sujankumarmitra.assetservice.v1.model.StoredAsset;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.READ;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.core.io.buffer.DataBufferUtils.readInputStream;
import static reactor.core.publisher.Mono.just;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@Slf4j
class LocalDiskBasedAssetStorageServiceTest {

    protected LocalDiskBasedAssetStorageService serviceUnderTest;

    private static final String VALID_ASSET_ID = "VALID_ASSET_ID";
    private static final String INVALID_ASSET_ID = "INVALID_ASSET_ID";
    private AssetStorageProperties mockStorageConfig;
    private AssetService mockAssetService;

    private StringBuilder bufferContentToStringBuilder(StringBuilder sb, DataBuffer buf) {
        while (buf.readableByteCount() > 0) {
            sb.append((char) buf.read());
        }
        return sb;
    }

    private void mapToInputStream(InputStreamSource streamSource, SynchronousSink<InputStream> sink) {
        try {
            sink.next(streamSource.getInputStream());
        } catch (IOException e) {
            sink.error(e);
        }
    }

    @BeforeEach
    void setUp() {
        mockStorageConfig();
        mockAssetService();

        serviceUnderTest = new LocalDiskBasedAssetStorageService(mockAssetService, mockStorageConfig);
    }

    private void mockAssetService() {
        mockAssetService = Mockito.mock(AssetService.class);
        Mockito.doReturn(just(new DefaultAsset(VALID_ASSET_ID, "hello.txt")))
                .when(mockAssetService)
                .getAsset(VALID_ASSET_ID);
    }

    private void mockStorageConfig() {
        mockStorageConfig = Mockito.mock(AssetStorageProperties.class);
        Mockito.doReturn("/tmp")
                .when(mockStorageConfig)
                .getBaseDir();
    }


    @Test
    void givenValidAssetId_whenStored_shouldStore() throws IOException {
        Flux<DataBuffer> dataBuffers = getSampleFileBuffer();
        Mono<Void> voidMono = serviceUnderTest.storeAsset(VALID_ASSET_ID, dataBuffers);

        StepVerifier.create(voidMono)
                .expectComplete()
                .verify();

        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(new File("/tmp/" + VALID_ASSET_ID));

        assertThat(scanner.next()).isEqualTo("Hello");
    }

    private Flux<DataBuffer> getSampleFileBuffer() {
        Path path = getSampleFilePath();
        return DataBufferUtils.read(
                path,
                new DefaultDataBufferFactory(),
                5, READ);
    }

    private Path getSampleFilePath() {
        return Path.of("src", "test", "resources", "hello.txt");
    }

    @Test
    void givenInvalidAssetId_whenStore_shouldCompleteWithError() {
        Mockito.doReturn(Mono.error(new AssetNotFoundException(INVALID_ASSET_ID)))
                .when(mockAssetService).getAsset(INVALID_ASSET_ID);

        Flux<DataBuffer> dataBuffers = getSampleFileBuffer();
        Mono<Void> voidMono = serviceUnderTest.storeAsset(INVALID_ASSET_ID, dataBuffers);

        StepVerifier.create(voidMono)
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(AssetNotFoundException.class);
                    log.info("Exception Thrown {}", (Object) err);
                })
                .verify();

    }

    @Test
    void givenValidAssetId_whenRetrieve_shouldRetrieve() throws IOException {
        copySampleFile();

        Mono<InputStreamSource> inputStreamSource = serviceUnderTest
                .retrieveAsset(VALID_ASSET_ID)
                .map(StoredAsset::getInputStreamSource);

        Mono<String> fileContent = inputStreamSource.handle(this::mapToInputStream)
                .flatMapMany(is -> readInputStream(() -> is, new DefaultDataBufferFactory(), 5))
                .reduceWith(StringBuilder::new, this::bufferContentToStringBuilder)
                .map(Object::toString);

        StepVerifier.create(fileContent)
                .expectNext("Hello")
                .expectComplete()
                .verify();
    }


    @Test
    void givenInvalidAssetId_whenRetrieve_shouldCompleteWithoutEmit() {
        Mockito.doReturn(Mono.empty())
                .when(mockAssetService).getAsset(INVALID_ASSET_ID);


        Mono<StoredAsset> storedAsset = serviceUnderTest.retrieveAsset(INVALID_ASSET_ID);

        StepVerifier.create(storedAsset)
                .expectComplete()
                .verify();
    }

    private void copySampleFile() throws IOException {
        Files.copy(
                getSampleFilePath(),
                Path.of("/tmp", VALID_ASSET_ID),
                REPLACE_EXISTING);
    }


    @Test
    void givenValidAssetId_whenPurge_shouldPurge() throws IOException {
        copySampleFile();
        Mono<Void> voidMono = serviceUnderTest.purgeAsset(VALID_ASSET_ID);

        StepVerifier.create(voidMono)
                .expectComplete()
                .verify();
    }

    @Test
    void givenValidAssetId_whenPurge_shouldComplete() {
        Mono<Void> voidMono = serviceUnderTest.purgeAsset(INVALID_ASSET_ID);

        StepVerifier.create(voidMono)
                .expectComplete()
                .verify();
    }


    @Test
    void givenValidAssetIdButNoAssociatedFile_whenRetrieveAsset_shouldEmitError() {
        Mockito.doReturn(Mono.just(new DefaultAsset(VALID_ASSET_ID, "somename")))
                .when(mockAssetService).getAsset(VALID_ASSET_ID);

        Mono<StoredAsset> storedAsset = serviceUnderTest.retrieveAsset(VALID_ASSET_ID);

        StepVerifier.create(storedAsset)
                .expectError()
                .verify();
    }
}