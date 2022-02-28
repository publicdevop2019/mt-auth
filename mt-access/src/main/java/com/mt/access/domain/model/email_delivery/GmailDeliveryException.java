package com.mt.access.domain.model.email_delivery;

public class GmailDeliveryException extends RuntimeException {
    public GmailDeliveryException(Throwable cause) {
        super("error during gmail deliver", cause);
    }
}
