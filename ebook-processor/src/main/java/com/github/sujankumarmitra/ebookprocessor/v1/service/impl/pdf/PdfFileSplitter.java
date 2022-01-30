package com.github.sujankumarmitra.ebookprocessor.v1.service.impl.pdf;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public interface PdfFileSplitter {

    List<Path> splitPdfFile(Path pdfFilePath) throws IOException;
}
