package com.mt.access.application.notification.representation;

import com.mt.access.domain.model.notification.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@Getter
public class NotificationRepresentation {
    private Long date;
    private String title;
    private Set<String> descriptions;

    public NotificationRepresentation(Notification notification) {
        date = notification.getTimestamp();
        descriptions = notification.getDescriptions();
        title = notification.getTitle();
    }
}
