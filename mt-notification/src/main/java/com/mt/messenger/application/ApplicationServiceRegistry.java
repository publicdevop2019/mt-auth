package com.mt.messenger.application;

import com.mt.common.domain.model.idempotent.IdempotentService;
import com.mt.messenger.application.email_delivery.EmailDeliveryApplicationService;
import com.mt.messenger.application.mall_notification.MallNotificationApplicationService;
import com.mt.messenger.application.system_notification.SystemNotificationApplicationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {

    @Getter
    private static EmailDeliveryApplicationService emailDeliverApplicationService;
    @Getter
    private static SystemNotificationApplicationService systemNotificationApplicationService;
    @Getter
    private static MallNotificationApplicationService mallNotificationApplicationService;
    @Getter
    private static IdempotentService idempotentService;

    @Autowired
    public void setEmailDeliverApplicationService(EmailDeliveryApplicationService emailDeliverApplicationService) {
        ApplicationServiceRegistry.emailDeliverApplicationService = emailDeliverApplicationService;
    }

    @Autowired
    public void setMallNotificationApplicationService(MallNotificationApplicationService mallNotificationApplicationService) {
        ApplicationServiceRegistry.mallNotificationApplicationService = mallNotificationApplicationService;
    }

    @Autowired
    public void setSystemNotificationApplicationService(SystemNotificationApplicationService systemNotificationApplicationService) {
        ApplicationServiceRegistry.systemNotificationApplicationService = systemNotificationApplicationService;
    }

    @Autowired
    public void setClientIdempotentApplicationService(IdempotentService clientIdempotentApplicationService) {
        ApplicationServiceRegistry.idempotentService = clientIdempotentApplicationService;
    }
}
