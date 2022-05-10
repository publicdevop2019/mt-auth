package com.mt.access.domain.model.notification;

public interface SmsNotificationService {
    void notify(String mobileNumber, String message);
}
