package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.user.UserId;

public interface WsPushNotificationService {
    void notifyMgmt(String message);

    void notifyUser(String message, UserId userId);
}
