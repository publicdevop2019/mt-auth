package com.mt.access.port.adapter.email;

import com.mt.access.domain.model.notification.EmailNotificationService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(
    value = "mt.email.type",
    havingValue = "noop",
    matchIfMissing = true)
public class NoOpEmailNotificationService implements EmailNotificationService {
    @Override
    public void notify(String deliverTo, String templateUrl, String subject,
                       Map<String, String> model) {
        log.info("skip sending email notifications");
    }
}
