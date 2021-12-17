package com.mt.messenger.application.system_notification;

import com.mt.messenger.domain.model.system_notification.SystemNotification;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SystemNotificationRepresentation {
    private Long date;
    private String message;

    public SystemNotificationRepresentation(Object o) {
        SystemNotification notification = (SystemNotification) o;
        date = notification.getTimestamp();
        message = notification.getDetails();
    }
}
