package com.mt.access.messenger.domain.service;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.messenger.application.mall_notification.MallNotificationRepresentation;
import com.mt.messenger.domain.DomainRegistry;
import com.mt.messenger.domain.model.mall_notification.MallNotification;
import com.mt.messenger.domain.model.mall_notification.MallNotificationId;
import org.springframework.stereotype.Service;

@Service
public class MallNotificationService {
    public MallNotificationId create(MallNotificationEvent deserialize) {
        MallNotification mallNotification = new MallNotification(deserialize);
        DomainRegistry.getMallNotificationRepository().add(mallNotification);
        DomainRegistry.getMallMonitorNotificationService().notify(CommonDomainRegistry.getCustomObjectSerializer().serialize(new MallNotificationRepresentation(mallNotification)));
        return mallNotification.getMallNotificationId();
    }
}
