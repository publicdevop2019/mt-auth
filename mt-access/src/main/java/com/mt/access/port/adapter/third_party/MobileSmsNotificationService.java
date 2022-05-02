package com.mt.access.port.adapter.third_party;

import com.mt.access.domain.model.notification.SmsNotificationService;
import org.springframework.stereotype.Service;

@Service
public class MobileSmsNotificationService implements SmsNotificationService {

    @Override
    public void notify(String mobileNumber, String message) {

    }
}
