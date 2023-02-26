package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;

public interface NotificationRepository {
    void add(Notification notification);

    void acknowledge(NotificationId notificationId);

    void acknowledgeForUser(NotificationId notificationId, UserId id);

    SumPagedRep<Notification> notificationsOfQuery(NotificationQuery notificationQuery);

    Optional<Notification> notificationOfId(NotificationId notificationId);
}
