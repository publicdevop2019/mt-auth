package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@NoArgsConstructor
@Data
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode
public class StoredEvent implements Serializable {
    @Lob
    private String eventBody;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //auto_increment id will not be continuous e.g. 1,2,3,5
    //due to transaction rollback in concurrent scenario.
    private Long id;
    private Long timestamp;
    private String name;
    private boolean internal;
    private boolean send = false;
    private String topic;
    private String domainId;

    private String applicationId;
    private boolean routable = true;
    private boolean rejected = false;

    public StoredEvent(DomainEvent event) {
        this.eventBody = CommonDomainRegistry.getCustomObjectSerializer().serialize(event);
        this.timestamp = event.getTimestamp();
        this.name = event.getName();
        this.internal = event.isInternal();
        this.topic = event.getTopic();
        this.applicationId = CommonDomainRegistry.getApplicationInfoService().getApplicationId();
        if (event.getDomainId() != null) {
            this.domainId = event.getDomainId().getDomainId();
        }
    }

    public void setIdExplicitly(long id) {
        this.id = id;
    }

    public void sendToMQ() {
        this.send = true;
    }

    public void markAsUnroutable() {
        this.routable = false;
    }

    public void markAsRejected() {
        this.rejected = true;
    }
}
