package com.github.sujankumarmitra.libraryservice.v1.model.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.Notification;
import lombok.Getter;
import lombok.Setter;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Getter
@Setter
public class DefaultNotification extends Notification {
    private String consumerId;
    private String payload;
    private Long createdAt;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
