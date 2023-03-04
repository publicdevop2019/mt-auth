package com.mt.access.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.function.Consumer;

public class ListenerHelper {
    public static <T extends DomainEvent> void listen(String name, Class<T> clazz,
                                                      Consumer<T> consumer) {
        DomainEvent domainEvent = new DomainEvent();
        domainEvent.getTopic();

        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, true, name, clazz, consumer);
    }

    public static <T extends DomainEvent> void listen2(T event, Consumer<T> consumer) {
        Class<? extends DomainEvent> aClass = event.getClass();
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, true, event.getTopic(), (Class<T>) aClass, consumer);
    }
}
