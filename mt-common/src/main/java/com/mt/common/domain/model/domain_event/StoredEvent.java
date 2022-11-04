package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

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
    //auto_increment id will not be continuous e.g. 1,2,3,5
    //due to transaction rollback in concurrent scenario.
    private Long id;
    private Long timestamp;
    private String name;
    private boolean internal;
    private boolean send = false;
    private String topic;
    private String domainId;

    @Value("${spring.application.name}")
    private String applicationId;
    private boolean routable = true;

    public StoredEvent(DomainEvent event) {
        this.eventBody = CommonDomainRegistry.getCustomObjectSerializer().serialize(event);
        this.timestamp = event.getTimestamp();
        this.name = event.getName();
        this.internal = event.isInternal();
        this.topic = event.getTopic();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StoredEvent that = (StoredEvent) o;
        return internal == that.internal && send == that.send
            &&
            Objects.equals(eventBody, that.eventBody)
            &&
            Objects.equals(id, that.id)
            &&
            Objects.equals(timestamp, that.timestamp)
            &&
            Objects.equals(name, that.name) && Objects.equals(topic, that.topic)
            &&
            Objects.equals(domainId, that.domainId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventBody, id, timestamp, name, internal, send, topic, domainId);
    }

    public void markAsUnroutable() {
        this.routable = false;
    }
}
