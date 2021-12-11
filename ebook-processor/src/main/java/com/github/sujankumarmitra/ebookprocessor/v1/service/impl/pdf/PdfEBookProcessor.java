package com.github.sujankumarmitra.ebookprocessor.v1.service.impl.pdf;

import com.github.sujankumarmitra.ebookprocessor.v1.model.*;
import com.github.sujankumarmitra.ebookprocessor.v1.model.impl.DefaultEBookProcessingStatus;
import com.github.sujankumarmitra.ebookprocessor.v1.model.impl.DefaultEBookSegment;
import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationToken;
import com.github.sujankumarmitra.ebookprocessor.v1.service.AssetServiceClient;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessingStatusService;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessor;
import com.github.sujankumarmitra.ebookprocessor.v1.service.LibraryServiceClient;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat.PDF;
import static com.github.sujankumarmitra.ebookprocessor.v1.model.ProcessingState.*;
import static java.nio.file.Files.deleteIfExists;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Service
@AllArgsConstructor
@Slf4j
public class PdfEBookProcessor implements EBookProcessor {
    @NonNull
    private final EBookProcessingStatusService statusService;
    @NonNull
    private final AssetServiceClient assetServiceClient;
    @NonNull
    private final LibraryServiceClient libraryServiceClient;
    @NonNull
    private final PdfFileSplitter pdfFileSplitter;

    @Override
    public boolean supports(EBookFormat format) {
        return format == PDF;
    }

    @Override
    public void process(EbookProcessDetails processDetails) {
        String processId = processDetails.getProcessId();
        Path bookPath = processDetails.getBookPath();
        String bookId = processDetails.getBookId();
        AuthenticationToken authToken = processDetails.getAuthToken();

        Mono.fromRunnable(() -> setStatusAsProcessing(processId))
                .then(libraryServiceClient
                        .deleteEBookSegments(bookId)
                        .doOnSuccess(s -> log.info("deleted existing ebook segments of bookId {} in library", bookId)))
                .thenMany(createPdfSplitFlux(bookPath))
                .concatMap(segmentPath -> assetServiceClient
                        .saveAsset(segmentPath)
                        .doOnNext(assetId -> log.debug("Created asset for segment {} with id {}", segmentPath, assetId))
                        .doOnError(err -> log.warn("Failed to create asset in asset-service", err))
                        .doOnTerminate(() -> deleteFile(segmentPath)))
                .doOnComplete(() -> log.info("Created assets for pdf segments"))
                .index()
                .map(tuple2 -> new DefaultEBookSegment(bookId, tuple2.getT1().intValue(), tuple2.getT2()))
                .cast(EBookSegment.class)
                .concatMap(segment -> libraryServiceClient
                        .saveEBookSegment(segment)
                        .doOnNext(segmentId -> log.debug("Created EBookSegment in library with id {}", segmentId)))
                .doOnDiscard(EBookSegment.class, this::deleteSavedAsset)
                .doOnComplete(() -> this.onSaveEBookSegmentComplete(processId))
                .doOnError(err -> this.onSaveEBookSegmentError(processId, err))
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken)) //@formatter:off
                .subscribe(it -> {}, err -> {}, () -> {}); // trigger execution
        //@formatter:on
    }

    private void deleteSavedAsset(EBookSegment segment) {
        // Delete saved asset as pipeline broke
    }

    private Flux<Path> createPdfSplitFlux(Path bookPath) {
        return Flux.create(sink -> {
            Path splitBasePath;
            try {
                splitBasePath = pdfFileSplitter.splitPdfFile(bookPath);
            } catch (IOException e) {
                log.warn("Error occurred while splitting pdf file", e);
                sink.error(e);
                return;
            }

            sink.onDispose(() -> {
                try {
                    Files.walk(splitBasePath, 1).forEach(this::deleteFile);
                } catch (IOException e) {
                    log.warn("Error while deleting Path entries", e);
                    // can be ignored
                }
            });

            Stream<Path> pathStream;
            try {
                pathStream = Files.walk(splitBasePath, 1).skip(1);
            } catch (IOException e) {
                log.warn("Error occurred while walking over file path");
                sink.error(e);
                return;
            }

            pathStream.forEach(sink::next);
            sink.complete();

        });
    }

    private void onSaveEBookSegmentError(String processId, Throwable err) {
        log.warn("Failed to save ebook segment in library service", err);
        setStatusAsFailed(processId, err);
    }

    private void onSaveEBookSegmentComplete(String processId) {
        log.info("Saved all ebook segments in library");
        setStatusAsCompleted(processId);
    }

    private void setStatusAsProcessing(String processId) {
        updateProcessingState(processId, PROCESSING, "Currently processing PDF file");
    }

    private void setStatusAsFailed(String processId, Throwable e) {
        updateProcessingState(processId, FAILED, e.getMessage());
    }

    private void setStatusAsCompleted(String processId) {
        updateProcessingState(processId, COMPLETED, "Completed EBook processing");
    }

    private void deleteFile(Path path) {
        log.debug("Deleting file {}", path);
        try {
            deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete segment", e);
//              can be ignored
        }
    }

    private void updateProcessingState(String processId, ProcessingState state, String message) {
        EBookProcessingStatus processingStatus = new DefaultEBookProcessingStatus(processId, state, message);

        statusService
                .saveStatus(processingStatus)
                .subscribe(s -> {
                        },
                        ex -> log.warn("Error saving EBookProcessingStatus ", ex), // no need to propagate status update errors
                        () -> log.info("Changed processing status of {} to {} state", processId, state));
    }

}
