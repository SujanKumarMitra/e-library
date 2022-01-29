package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.config.EBookProcessorProperties;
import com.github.sujankumarmitra.ebookprocessor.v1.exception.EBookFormatNotSupportedException;
import com.github.sujankumarmitra.ebookprocessor.v1.exception.EBookNotFoundException;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBook;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookProcessRequest;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EbookProcessDetails;
import com.github.sujankumarmitra.ebookprocessor.v1.model.impl.DefaultEBookProcessingStatus;
import com.github.sujankumarmitra.ebookprocessor.v1.model.impl.DefaultEbookProcessDetails;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessingService;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessingStatusService;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessor;
import com.github.sujankumarmitra.ebookprocessor.v1.service.LibraryServiceClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.sujankumarmitra.ebookprocessor.v1.model.ProcessingState.PENDING;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultEBookProcessingService implements EBookProcessingService, InitializingBean {
    @NonNull
    private final EBookProcessingStatusService statusService;
    @NonNull
    private final EBookProcessor processor;
    @NonNull
    private final LibraryServiceClient libraryServiceClient;
    @NonNull
    private final EBookProcessorProperties properties;
    private Scheduler processorScheduler;

    @Override
    public Mono<String> submitProcess(EBookProcessRequest processRequest) {
        String eBookId = processRequest.getEBookId();
        return libraryServiceClient
                .getEBook(eBookId)
                .switchIfEmpty(Mono.error(() -> new EBookNotFoundException(eBookId)))
                .handle(this::emitErrorIfFormatNotSupported)
                .flatMap(eBook -> saveEBookToDisk(eBook, processRequest))
                .flatMap(this::setStatusToPending)
                .map(tuple2 -> createProcessDetails(tuple2, processRequest))
                .doOnNext(details -> processorScheduler.schedule(() -> processor.process(details)))
                .map(EbookProcessDetails::getProcessId);
    }

    private EbookProcessDetails createProcessDetails(Tuple2<EBook, Path> tuple2, EBookProcessRequest request) {
        DefaultEbookProcessDetails processDetails = new DefaultEbookProcessDetails();

        EBook ebook = tuple2.getT1();
        Path bookPath = tuple2.getT2();

        processDetails.setProcessId(ebook.getLibraryId() + ":" + ebook.getId());
        processDetails.setBookId(ebook.getId());
        processDetails.setBookPath(bookPath);
        processDetails.setAuthToken(request.getToken());

        return processDetails;
    }

    private Mono<Tuple2<EBook, Path>> setStatusToPending(Tuple2<EBook, Path> tuple2) {
        String pathName = tuple2.getT2().getFileName().toString();

        DefaultEBookProcessingStatus processingStatus = new DefaultEBookProcessingStatus();

        processingStatus.setState(PENDING);
        processingStatus.setProcessId(pathName);
        processingStatus.setMessage("Currently waiting to be picked");

        return statusService
                .saveStatus(processingStatus)
                .thenReturn(tuple2);
    }

    private Mono<Tuple2<EBook, Path>> saveEBookToDisk(EBook eBook, EBookProcessRequest processRequest) {
        Path ebookPath;
        try {
            ebookPath = Files.createTempFile("", "");
        } catch (IOException ex) {
            log.warn("Error in creating temp file:: ", ex);
            return Mono.error(ex);
        }

        return DataBufferUtils
                .write(processRequest.getEBookBufferFlux(), ebookPath)
                .thenReturn(Tuples.of(eBook, ebookPath));

    }

    private void emitErrorIfFormatNotSupported(EBook eBook, SynchronousSink<EBook> sink) {
        EBookFormat format = eBook.getFormat();
        if (!processor.supports(format)) {
            sink.error(new EBookFormatNotSupportedException(format));
        } else {
            sink.next(eBook);
        }
    }

    @Override
    public void afterPropertiesSet() {
        int threadCapacity = properties.getThreadPoolCapacity();
        processorScheduler = Schedulers.newBoundedElastic(threadCapacity, Integer.MAX_VALUE, "EBookProcessingService");
    }
}
