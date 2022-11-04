package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.model.domain_id.DomainId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnrountableMessageEvent extends DomainEvent {
    public static final String UNROUTABLE_MSG_EVENT = "unroutable_msg_event";
    public static final String name = "UNROUTABLE_MSG_EVENT";
    String topic;

    public UnrountableMessageEvent(StoredEvent event) {
        super();
        setTopic(UNROUTABLE_MSG_EVENT);
        setName(name);
        this.topic = event.getTopic();
        this.setDomainId(new AnyDomainId(event.getDomainId()));

    }

    @NoArgsConstructor
    private static class AnyDomainId extends DomainId {
        public AnyDomainId(String domainId) {
            super(domainId);
        }
    }

}
