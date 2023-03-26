package com.mt.access.port.adapter.sms;

import com.mt.access.domain.model.notification.SmsNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    value="mt.sms.service.type",
    havingValue = "noop",
    matchIfMissing = true)
public class NoOpSmsNotificationService implements SmsNotificationService {
    @Override
    public void notify(String mobileNumber, String message) {
        log.info("skip sending sms message to user mobile");
    }
}
