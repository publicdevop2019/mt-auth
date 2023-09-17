package com.mt.access.application.notification.representation;

import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.notification.event.SendBellNotificationEvent;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BellNotificationRepresentation {
    private Long date;
    private String title;
    private String id;
    private Set<String> descriptions;

    public BellNotificationRepresentation(SendBellNotificationEvent notification) {
        date = notification.getTimestamp();
        id = notification.getDomainId().getDomainId();
        descriptions = notification.getDescriptions();
        title = notification.getTitle();
    }

    public BellNotificationRepresentation(Notification notification) {
        date = notification.getTimestamp();
        id = notification.getNotificationId().getDomainId();
        descriptions = notification.getDescriptions();
        title = notification.getTitle();
    }

}
