package com.github.sujankumarmitra.ebookprocessor.v1.model.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookProcessingStatus;
import com.github.sujankumarmitra.ebookprocessor.v1.model.ProcessingState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultEBookProcessingStatus extends EBookProcessingStatus {
    private String processId;
    private ProcessingState state;
    private String message;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
