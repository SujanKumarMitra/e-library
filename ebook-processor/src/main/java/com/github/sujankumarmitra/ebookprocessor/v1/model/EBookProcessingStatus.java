package com.github.sujankumarmitra.ebookprocessor.v1.model;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public abstract class EBookProcessingStatus {

    public abstract String getProcessId();

    public abstract ProcessingState getState();

    public abstract String getMessage();

}
