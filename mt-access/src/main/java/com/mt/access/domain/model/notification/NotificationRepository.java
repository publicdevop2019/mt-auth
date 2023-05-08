package com.mt.access.domain.model.notification;

import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;

public interface NotificationRepository {
    void add(Notification notification);

    void acknowledge(NotificationId notificationId);

    void acknowledgeForUser(NotificationId notificationId, UserId id);

    SumPagedRep<Notification> notificationsOfQuery(NotificationQuery notificationQuery);

    default Notification by(NotificationId notificationId){
        Notification notification = byNullable(notificationId);
        Validator.notNull(notification);
        return notification;
    }

    Notification byNullable(NotificationId notificationId);
}
