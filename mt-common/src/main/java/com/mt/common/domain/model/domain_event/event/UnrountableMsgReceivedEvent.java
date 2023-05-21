package com.mt.common.domain.model.domain_event.event;

import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.StoredEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UnrountableMsgReceivedEvent extends DomainEvent {
    public static final String UNROUTABLE_MSG_EVENT = "unroutable_msg_event";
    public static final String name = "UNROUTABLE_MSG_EVENT";
    private String sourceTopic;
    private String sourceName;
    private long sourceEventId;
    {
        setTopic(UNROUTABLE_MSG_EVENT);
        setName(name);

    }
    public UnrountableMsgReceivedEvent(StoredEvent event) {
        super(new AnyDomainId());
        this.sourceTopic = event.getTopic();
        this.sourceName = event.getName();
        this.sourceEventId = event.getId();
    }

}
