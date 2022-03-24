package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Getter
@NoArgsConstructor
@Data
@Setter(AccessLevel.PRIVATE)
public class StoredEvent implements Serializable {
    @Lob
    private String eventBody;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //auto_increment id will not be continuous e.g. 1,2,3,5 due to transaction rollback in concurrent scenario
    private Long id;
    private Long timestamp;
    private String name;
    private boolean internal;
    private boolean send = false;
    private String topic;
    private String domainId;

    public StoredEvent(DomainEvent aDomainEvent) {
        this.eventBody = CommonDomainRegistry.getCustomObjectSerializer().serialize(aDomainEvent);
        this.timestamp = aDomainEvent.getTimestamp();
        this.name = aDomainEvent.getName();
        this.internal = aDomainEvent.isInternal();
        this.topic = aDomainEvent.getTopic();
        if (aDomainEvent.getDomainId() != null)
            this.domainId = aDomainEvent.getDomainId().getDomainId();
    }

    public void setIdExplicitly(long id) {
        this.id = id;
    }
    public void sendToMQ() {
        this.send = true;
    }

}
