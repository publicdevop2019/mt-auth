package com.mt.access.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.function.Consumer;

public class ListenerHelper {
    public static <T extends DomainEvent> void listen(T event, Consumer<T> consumer) {
        Class<? extends DomainEvent> aClass = event.getClass();
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, true, event.getTopic(), (Class<T>) aClass, consumer);
    }
}
