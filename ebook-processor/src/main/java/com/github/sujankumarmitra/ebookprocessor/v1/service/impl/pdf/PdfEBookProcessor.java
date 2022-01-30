package com.github.sujankumarmitra.ebookprocessor.v1.service.impl.pdf;

import com.github.sujankumarmitra.ebookprocessor.v1.model.*;
import com.github.sujankumarmitra.ebookprocessor.v1.model.impl.DefaultAsset;
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
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.github.sujankumarmitra.ebookprocessor.v1.model.AccessLevel.PRIVATE;
import static com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat.PDF;
import static com.github.sujankumarmitra.ebookprocessor.v1.model.ProcessingState.*;
import static java.nio.file.Files.deleteIfExists;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

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
    private static final Scheduler WORKER = Schedulers.newSingle("EBookProcessor");

    @Override
    public boolean supports(EBookFormat format) {
        return format == PDF;
    }

    @Override
    public void process(EbookProcessDetails processDetails) {
        String processId = processDetails.getProcessId();
        Path bookPath = processDetails.getBookPath();
        EBook eBook = processDetails.getEBook();
        AuthenticationToken authToken = processDetails.getAuthToken();

        Mono.fromRunnable(() -> setStatusAsProcessing(processId))
                .then(deleteExistingEBookSegments(eBook))
                .thenMany(createPdfSplit(bookPath))
                .doOnTerminate(() -> deleteFile(bookPath))
                .index()
                .concatMap(tuple2 -> saveAsset(tuple2, eBook)
                        .doOnTerminate(() -> deleteFile(tuple2.getT2())))
                .doOnComplete(() -> log.info("Created assets for pdf segments"))
                .map(tuple2 -> new DefaultEBookSegment(eBook.getId(), tuple2.getT1().intValue(), tuple2.getT2()))
                .cast(EBookSegment.class)
                .concatMap(this::saveToLibrary)
                .doOnDiscard(EBookSegment.class, this::deleteSavedAsset)
                .doOnComplete(() -> this.onSaveEBookSegmentComplete(processId))
                .doOnError(err -> this.onSaveEBookSegmentError(processId, err))
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken))
                //@formatter:off
                .subscribe(it -> {}, err -> {}, () -> {}); // trigger execution
        //@formatter:on
    }

    private Mono<Tuple2<Long, String>> saveAsset(Tuple2<Long, Path> indexedPath, EBook book) {
        long index = indexedPath.getT1();
        Path segmentPath = indexedPath.getT2();

        Asset asset = new DefaultAsset(book.getId() + "_" + index, book.getLibraryId(), APPLICATION_PDF_VALUE, PRIVATE);
        return assetServiceClient
                .createAsset(asset)
                .flatMap(assetId -> assetServiceClient
                        .storeAsset(assetId, segmentPath)
                        .thenReturn(Tuples.of(index, assetId)))
                .publishOn(WORKER)
                .doOnNext(tuple2 -> log.debug("Created asset for segment {} with id {}", segmentPath, tuple2.getT2()))
                .doOnError(err -> log.warn("Failed to create asset in asset-service", err))
                .doOnTerminate(() -> deleteFile(segmentPath));
    }

    private Mono<String> saveToLibrary(EBookSegment segment) {
        return libraryServiceClient
                .saveEBookSegment(segment)
                .publishOn(WORKER)
                .doOnNext(segmentId -> log.debug("Created EBookSegment in library with id {}", segmentId));
    }

    private Mono<Void> deleteExistingEBookSegments(EBook eBook) {
        return libraryServiceClient
                .deleteEBookSegments(eBook.getId())
                .publishOn(WORKER)
                .doOnSuccess(s -> log.info("deleted existing ebook segments of bookId {} in library", eBook.getId()));
    }

    private void deleteSavedAsset(EBookSegment segment) {
        // Delete saved asset as pipeline broke
    }

    public Flux<Path> createPdfSplit(Path bookPath) {
        return Flux.defer(() -> {
            try {
                List<Path> splits = pdfFileSplitter.splitPdfFile(bookPath);
                log.info("Created total {} PDF splits for bookPath {}", splits.size(), bookPath);
                return Flux.fromIterable(splits);
            } catch (IOException e) {
                log.warn("Error occurred while splitting pdf file", e);
                return Flux.error(e);
            }
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
                .publishOn(WORKER)
                .subscribe(s -> {
                        },
                        ex -> log.warn("Error saving EBookProcessingStatus ", ex), // no need to propagate status update errors
                        () -> log.info("Changed processing status of {} to {} state", processId, state));
    }


}
