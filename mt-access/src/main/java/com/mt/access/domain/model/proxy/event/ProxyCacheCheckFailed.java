package com.mt.access.domain.model.proxy.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;

@AuditEvent
public class ProxyCacheCheckFailed extends DomainEvent {
    public static final String PROXY_CACHE_CHECK_FAILED_EVENT = "proxy_cache_check_failed_event";
    public static final String name = "PROXY_CACHE_CHECK_FAILED_EVENT";

    {
        setTopic(PROXY_CACHE_CHECK_FAILED_EVENT);
        setName(name);
    }

    public ProxyCacheCheckFailed() {
        super(new AnyDomainId());
    }
}
