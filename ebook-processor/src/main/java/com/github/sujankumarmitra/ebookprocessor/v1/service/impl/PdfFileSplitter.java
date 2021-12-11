package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public interface PdfFileSplitter {

    Path splitPdfFile(Path pdfFilePath) throws IOException;
}
