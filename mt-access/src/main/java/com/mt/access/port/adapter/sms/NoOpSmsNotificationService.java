package com.mt.access.port.adapter.sms;

import com.mt.access.domain.model.i18n.SupportedLocale;
import com.mt.access.domain.model.notification.SmsNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    value = "mt.sms.type",
    havingValue = "noop",
    matchIfMissing = true)
public class NoOpSmsNotificationService implements SmsNotificationService {
    @Override
    public void notify(String countryCode, String mobileNumber, String code, SupportedLocale locale) {
        log.info("skip sending sms message to user mobile");
    }
}
