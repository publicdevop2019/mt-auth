package com.mt.access.application.notification.representation;

import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.notification.NotificationStatus;
import com.mt.access.domain.model.notification.NotificationType;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotificationRepresentation {
    private Long date;
    private String id;
    private String title;
    private Set<String> descriptions;
    private NotificationType type;
    private NotificationStatus status;

    public NotificationRepresentation(Notification notification) {
        date = notification.getTimestamp();
        id = notification.getNotificationId().getDomainId();
        descriptions = notification.getDescriptions();
        title = notification.getTitle();
        type = notification.getType();
        status = notification.getStatus();
    }
}
