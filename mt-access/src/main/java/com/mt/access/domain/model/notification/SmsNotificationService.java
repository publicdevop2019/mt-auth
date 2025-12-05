package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.i18n.SupportedLocale;

public interface SmsNotificationService {
    void notify(String countryCode, String mobileNumber, String code, SupportedLocale locale);
}
