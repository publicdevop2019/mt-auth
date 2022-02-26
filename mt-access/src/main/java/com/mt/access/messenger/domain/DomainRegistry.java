package com.mt.access.messenger.domain;

import com.mt.messenger.domain.model.mall_notification.MallNotificationRepository;
import com.mt.messenger.domain.model.system_notification.SystemNotificationRepository;
import com.mt.messenger.domain.service.MallMonitorNotificationService;
import com.mt.messenger.domain.service.MallNotificationService;
import com.mt.messenger.domain.service.SystemMonitorNotificationService;
import com.mt.messenger.domain.service.SystemNotificationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DomainRegistry {
    @Getter
    private static SystemMonitorNotificationService systemMonitorNotificationService;
    @Getter
    private static MallMonitorNotificationService mallMonitorNotificationService;
    @Getter
    private static SystemNotificationService systemNotificationService;
    @Getter
    private static SystemNotificationRepository systemNotificationRepository;
    @Getter
    private static MallNotificationRepository mallNotificationRepository;
    @Getter
    private static MallNotificationService mallNotificationService;

    @Autowired
    public void setMallNotificationRepository(MallNotificationRepository mallNotificationRepository) {
        DomainRegistry.mallNotificationRepository = mallNotificationRepository;
    }

    @Autowired
    public void setMallMonitorNotificationService(MallMonitorNotificationService mallMonitorNotificationService) {
        DomainRegistry.mallMonitorNotificationService = mallMonitorNotificationService;
    }

    @Autowired
    public void setMallNotificationService(MallNotificationService mallNotificationService) {
        DomainRegistry.mallNotificationService = mallNotificationService;
    }

    @Autowired
    public void setSystemMonitorNotificationService(SystemMonitorNotificationService userNotificationService) {
        DomainRegistry.systemMonitorNotificationService = userNotificationService;
    }

    @Autowired
    public void setSystemNotificationService(SystemNotificationService systemNotificationService) {
        DomainRegistry.systemNotificationService = systemNotificationService;
    }

    @Autowired
    public void setSystemNotificationRepository(SystemNotificationRepository systemNotificationRepository) {
        DomainRegistry.systemNotificationRepository = systemNotificationRepository;
    }

}
