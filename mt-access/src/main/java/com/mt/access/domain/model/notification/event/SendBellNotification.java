package com.mt.access.domain.model.notification.event;

import com.mt.access.application.notification.representation.BellNotificationRepresentation;
import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.LinkedHashSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SendBellNotification extends DomainEvent {
    public static final String SEND_BELL_NOTIFICATION_EVENT =
        "send_bell_notification_event";
    public static final String name = "SEND_BELL_NOTIFICATION_EVENT";
    private Long date;
    private String title;
    private LinkedHashSet<String> descriptions;
    private UserId userId;
    private String traceId;

    {
        setTopic(SEND_BELL_NOTIFICATION_EVENT);
        setName(name);
    }

    public SendBellNotification(Notification notification) {
        super(notification.getNotificationId());
        date = notification.getTimestamp();
        this.descriptions = notification.getDescriptions();
        title = notification.getTitle();
        userId = notification.getUserId();
        traceId = notification.getTraceId();
    }

    public String value() {
        return CommonDomainRegistry.getCustomObjectSerializer()
            .serialize(new BellNotificationRepresentation(this));
    }

}
