package com.mt.access.domain.model.notification;

public interface SmsNotificationService {
    void notify(String countryCode,String mobileNumber, String code);
}
