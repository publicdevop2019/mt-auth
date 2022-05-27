package com.mt.access.domain.model.notification;

public class EmailNotificationException extends RuntimeException {
    public EmailNotificationException(Throwable cause) {
        super("error during gmail deliver", cause);
    }
}
