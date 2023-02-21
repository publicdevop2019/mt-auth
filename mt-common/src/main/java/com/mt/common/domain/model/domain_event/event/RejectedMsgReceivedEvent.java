package com.mt.common.domain.model.domain_event.event;

import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.StoredEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RejectedMsgReceivedEvent extends DomainEvent {
    public static final String REJECTED_MSG_EVENT = "rejected_msg_event";
    public static final String name = "REJECTED_MSG_EVENT";
    private String sourceTopic;
    private String sourceName;
    private long sourceEventId;
    public RejectedMsgReceivedEvent(StoredEvent event) {
        super();
        setTopic(REJECTED_MSG_EVENT);
        setName(name);
        this.sourceTopic = event.getTopic();
        this.sourceName = event.getName();
        this.sourceEventId = event.getId();
        this.setDomainId(new AnyDomainId(event.getDomainId()));

    }

}
