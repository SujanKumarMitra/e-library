package com.github.mitrakumarsujan.notificationservice.v1.exception;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
public class NotificationNotFoundException extends ApiOperationException {

    private final Collection<ErrorDetails> errors;

    public NotificationNotFoundException(String notificationId) {
        this.errors = List.of(new DefaultErrorDetails("Notification with id {" + notificationId + "} not found"));
    }

    @Override
    public Collection<ErrorDetails> getErrors() {
        return this.errors;
    }
}
