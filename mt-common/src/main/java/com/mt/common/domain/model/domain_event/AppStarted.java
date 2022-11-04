package com.mt.common.domain.model.domain_event;

public class AppStarted {
    public static StoredEvent create() {
        DomainEvent domainEvent = new DomainEvent();
        domainEvent.setName("ApplicationStarted");
        return new StoredEvent(domainEvent);
    }
    private AppStarted(){

    }
}
