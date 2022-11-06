package com.mt.common.domain.model.domain_event.event;

import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.StoredEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnrountableMsgReceivedEvent extends DomainEvent {
    public static final String UNROUTABLE_MSG_EVENT = "unroutable_msg_event";
    public static final String name = "UNROUTABLE_MSG_EVENT";
    String topic;

    public UnrountableMsgReceivedEvent(StoredEvent event) {
        super();
        setTopic(UNROUTABLE_MSG_EVENT);
        setName(name);
        this.topic = event.getTopic();
        this.setDomainId(new AnyDomainId(event.getDomainId()));

    }

}
