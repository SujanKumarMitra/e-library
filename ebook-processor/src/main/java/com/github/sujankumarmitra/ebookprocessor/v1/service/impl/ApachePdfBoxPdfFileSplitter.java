package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.config.EBookProcessorProperties;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import static java.nio.file.Files.createTempFile;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Component
@Slf4j
@AllArgsConstructor
public class ApachePdfBoxPdfFileSplitter implements PdfFileSplitter {

    public static final MemoryUsageSetting MEM_USAGE_SETTING = MemoryUsageSetting.setupTempFileOnly();
    @NonNull
    private final EBookProcessorProperties processorProperties;

    @Override
    public Path splitPdfFile(Path pdfFilePath) throws IOException {
        PDDocument document = PDDocument.load(pdfFilePath.toFile(), MEM_USAGE_SETTING);
        Path splitBasePath = Files.createTempDirectory("");
        int maxSplitSize = processorProperties.getMaxSegmentSize();

        Iterator<PDPage> pageIterator = document.getPages().iterator();
        while (pageIterator.hasNext()) {
            PDDocument pdfSplit = new PDDocument();
            int splitSize = maxSplitSize;
            while (pageIterator.hasNext() && splitSize > 0) {
                pdfSplit.addPage(pageIterator.next());
                splitSize--;
            }
            Path basePath = createTempFile(splitBasePath, "", "");
            pdfSplit.save(basePath.toFile());
            pdfSplit.close();  // TODO test closing effect
        }

        document.close();
        return splitBasePath;
    }


}
