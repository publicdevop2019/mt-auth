package com.mt.access.domain.model.proxy.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.common.domain.model.domain_event.DomainEvent;

public class ProxyCacheCheckFailedEvent extends DomainEvent implements AuditEvent {
    public static final String PROXY_CACHE_CHECK_FAILED_EVENT = "proxy_cache_check_failed_event";
    public static final String name = "PROXY_CACHE_CHECK_FAILED_EVENT";

    public ProxyCacheCheckFailedEvent() {
        super();
        setTopic(PROXY_CACHE_CHECK_FAILED_EVENT);
        setName(name);
    }
}
