package com.mt.access.messenger.application.mall_notification;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.messenger.domain.model.mall_notification.MallNotification;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@Getter
public class MallNotificationRepresentation implements Serializable {
    private Long date;
    private Map<String, String> detail;
    private String orderId;
    private String changeId;
    private String name;

    public MallNotificationRepresentation(Object o) {
        MallNotification notification = (MallNotification) o;
        date = notification.getTimestamp();
        orderId=notification.getOrderId();
        name=notification.getName();
        changeId=notification.getChangeId();
        detail= CommonDomainRegistry.getCustomObjectSerializer().deserializeToMap(notification.getDetails(), String.class, String.class);

    }
}
