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
    //db generated id will make sure event get read in order and will not get skipped
    private Long id;
    private Long timestamp;
    private String name;
    private boolean internal;
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

}
