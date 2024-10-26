package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;

public interface NotificationRepository {
    void add(Notification notification);

    void acknowledge(NotificationId notificationId);

    void acknowledgeForUser(NotificationId notificationId, UserId id);

    SumPagedRep<Notification> notificationsOfQuery(NotificationQuery notificationQuery);

    default Notification get(NotificationId notificationId) {
        Notification notification = query(notificationId);
        Validator.notNull(notification);
        return notification;
    }

    Notification query(NotificationId notificationId);

    void markAsDelivered(NotificationId notificationId);
}
