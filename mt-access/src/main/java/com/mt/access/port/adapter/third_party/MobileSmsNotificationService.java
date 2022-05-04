package com.mt.access.port.adapter.third_party;

import com.mt.access.domain.model.notification.SmsNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MobileSmsNotificationService implements SmsNotificationService {

    @Override
    public void notify(String mobileNumber, String message) {
        log.info("[todo] sending sms message to user mobile");
    }
}
