package com.mt.access.domain.model.notification;

import java.util.Map;

public interface EmailNotificationService {
    void notify(String deliverTo, String templateUrl, String subject, Map<String, String> model);
}
