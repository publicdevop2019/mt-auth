package com.mt.access.domain.model.notification.event;

import com.mt.access.application.notification.representation.BellNotificationRepresentation;
import com.mt.access.domain.model.notification.Notification;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.Set;
import lombok.Getter;

@Getter
public class SendBellNotificationEvent extends DomainEvent {
    public static final String SEND_BELL_NOTIFICATION_EVENT =
        "send_bell_notification_event";
    public static final String name = "SEND_BELL_NOTIFICATION_EVENT";
    private final Long date;
    private final String title;
    private final String id;
    private final Set<String> descriptions;

    public SendBellNotificationEvent(Notification notification2) {
        super();
        setTopic(SEND_BELL_NOTIFICATION_EVENT);
        setName(name);
        setDomainId(notification2.getNotificationId());
        date = notification2.getTimestamp();
        id = notification2.getNotificationId().getDomainId();
        descriptions = notification2.getDescriptions();
        title = notification2.getTitle();
    }

    public String value() {
        return CommonDomainRegistry.getCustomObjectSerializer()
            .serialize(new BellNotificationRepresentation(this));
    }

}
