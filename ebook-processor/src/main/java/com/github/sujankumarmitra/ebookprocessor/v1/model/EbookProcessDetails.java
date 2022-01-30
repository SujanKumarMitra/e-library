package com.github.sujankumarmitra.ebookprocessor.v1.model;

import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationToken;

import java.nio.file.Path;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public abstract class EbookProcessDetails {

    public abstract String getProcessId();

    public abstract EBook getEBook();

    public abstract Path getBookPath();

    public abstract AuthenticationToken getAuthToken();

    @Override
    public String toString() {
        return "EbookProcessDetails{" +
                "processId='" + getProcessId() + '\'' +
                ", bookId='" + getEBook() + '\'' +
                ", bookLocation=" + getBookPath() +
                ", authToken=" + getAuthToken() +
                '}';
    }

}
