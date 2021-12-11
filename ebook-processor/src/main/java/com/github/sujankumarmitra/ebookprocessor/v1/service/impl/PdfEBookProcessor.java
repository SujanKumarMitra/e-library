package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.config.EBookProcessorProperties;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookProcessingStatus;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EbookProcessDetails;
import com.github.sujankumarmitra.ebookprocessor.v1.model.ProcessingState;
import com.github.sujankumarmitra.ebookprocessor.v1.model.impl.DefaultEBookProcessingStatus;
import com.github.sujankumarmitra.ebookprocessor.v1.model.impl.DefaultEBookSegment;
import com.github.sujankumarmitra.ebookprocessor.v1.service.AssetServiceClient;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessingStatusService;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessor;
import com.github.sujankumarmitra.ebookprocessor.v1.service.LibraryServiceClient;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat.PDF;
import static com.github.sujankumarmitra.ebookprocessor.v1.model.ProcessingState.*;
import static java.nio.file.Files.*;
import static org.apache.pdfbox.pdmodel.PDDocument.load;
import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Service
@AllArgsConstructor
@Slf4j
public class PdfEBookProcessor implements EBookProcessor {
    public static final MemoryUsageSetting MEM_USAGE_SETTING = MemoryUsageSetting.setupTempFileOnly();
    @NonNull
    private final EBookProcessorProperties processorProperties;
    @NonNull
    private final EBookProcessingStatusService statusService;
    @NonNull
    private final AssetServiceClient assetServiceClient;
    @NonNull
    private final LibraryServiceClient libraryServiceClient;

    @Override
    public boolean supports(EBookFormat format) {
        return format == PDF;
    }

    @Override
    public void process(EbookProcessDetails processDetails) {
        String processId = processDetails.getProcessId();
        setStatusAsProcessing(processId);

//         break giant pdf to small pdfs segments
        List<PDDocument> pdfSegments;
        try {
            pdfSegments = createPdfSegments(processDetails.getBookLocation());
        } catch (IOException e) {
            log.warn("Failed to create ebook segments", e);
            setStatusAsFailed(processId, e);
            return;
        }
//          save pdf segments in temp storage
        List<Path> pdfSegmentPaths;
        try {
            pdfSegmentPaths = saveSegmentsToDisk(pdfSegments);
        } catch (IOException e) {
            log.warn("Error while saving segments to disk", e);
            setStatusAsFailed(processId, e);
            return;
        }
//          @formatter:off
        this.saveSegmentInAssetService(pdfSegmentPaths) // save segments in asset service
                .as(it -> saveAssetIdsInLibraryService(it, processDetails)) // save assetIds in library service
                .contextWrite(withAuthentication(processDetails.getAuthToken()))
                .subscribe(it -> {}, err -> {}, () -> {}); // trigger execution
//          @formatter:on
    }

    protected Flux<String> saveAssetIdsInLibraryService(Flux<String> assetIdFlux, EbookProcessDetails processDetails) {
        String processId = processDetails.getProcessId();
        String bookId = processDetails.getBook().getId();

        return assetIdFlux
                .index()
                .map(tuple2 -> new DefaultEBookSegment(bookId, tuple2.getT1().intValue(), tuple2.getT2()))
                .concatMap(libraryServiceClient::saveEBookSegment)
                .doOnNext(segmentId -> log.info("Created EBookSegment in library with id {}", segmentId))
                .doOnError(err -> this.onSaveEBookSegmentError(processId, err))
                .doOnComplete(() -> this.onSaveEBookSegmentComplete(processId));
    }

    protected Flux<String> saveSegmentInAssetService(List<Path> pdfSegmentPaths) {
        return Flux
                .fromIterable(pdfSegmentPaths)
                .concatMap(segmentPath -> assetServiceClient
                        .saveAsset(segmentPath)
                        .doOnNext(assetId -> log.info("Created asset for segment {} with id {}", segmentPath, assetId))
                        .doOnError(err -> log.warn("Failed to create asset in asset-service", err))
                        .doOnTerminate(() -> deletePdfSegment(segmentPath)))
                .doOnComplete(() -> log.info("Created assets for pdf segments"));
    }

    protected List<PDDocument> createPdfSegments(Path bookLocation) throws IOException {
        PDDocument document = load(bookLocation.toFile(), MEM_USAGE_SETTING);
        return createSegments(document);
    }

    protected List<Path> saveSegmentsToDisk(List<PDDocument> segments) throws IOException {
        Path segmentsBasePath = createTempDirectory("");

        List<Path> segmentPaths = new ArrayList<>();
        for (PDDocument segment : segments) {
            Path segmentPath = createTempFile(segmentsBasePath, "", "");

            segment.save(segmentPath.toFile());
            segmentPaths.add(segmentPath);

            segment.close();
        }
        return segmentPaths;
    }

    protected List<PDDocument> createSegments(PDDocument document) {
        List<PDDocument> segments = new ArrayList<>();
        Iterator<PDPage> pageIterator = document.getPages().iterator();

        while (pageIterator.hasNext()) {
            int segmentSize = processorProperties.getMaxSegmentSize();
            PDDocument segment = new PDDocument();

            while (pageIterator.hasNext() && segmentSize > 0) {
                segment.addPage(pageIterator.next());
                segmentSize--;
            }
            segments.add(segment);
        }
        return segments;
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

    private void deletePdfSegment(Path path) {
        try {
            deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete segment", e);
//              can be ignored
        }
    }

    private void deleteOriginalPdf(Path location, PDDocument document) throws IOException {
        document.close();
        deleteIfExists(location);
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
