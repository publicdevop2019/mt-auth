package com.mt.access.port.adapter.email;

public class GmailDeliveryException extends RuntimeException {
    public GmailDeliveryException(Throwable cause) {
        super("error during gmail deliver", cause);
    }
}
