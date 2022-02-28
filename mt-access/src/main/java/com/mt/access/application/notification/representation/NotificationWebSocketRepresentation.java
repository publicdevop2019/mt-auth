package com.mt.access.application.notification.representation;

import com.mt.access.domain.model.notification.Notification;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@Getter
public class NotificationWebSocketRepresentation {
    private Long date;
    private String title;
    private Set<String> descriptions;

    public NotificationWebSocketRepresentation(Notification notification) {
        date = notification.getTimestamp();
        descriptions = notification.getDescriptions();
        title = notification.getTitle();
    }

    public String value() {
        return CommonDomainRegistry.getCustomObjectSerializer().serialize(this);
    }
}
