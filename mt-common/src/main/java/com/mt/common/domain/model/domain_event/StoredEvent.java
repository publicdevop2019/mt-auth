package com.mt.common.domain.model.domain_event;

import static com.mt.common.domain.model.constant.AppInfo.TRACE_ID_LOG;

import com.mt.common.domain.CommonDomainRegistry;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.MDC;

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
    private Long id;
    private Long timestamp;
    private String name;
    private Boolean internal;
    private Boolean send = false;
    private String topic;
    private String domainId;

    private String applicationId;
    private String traceId;
    private Boolean routable = true;
    private Boolean rejected = false;

    public StoredEvent(DomainEvent event) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.eventBody = CommonDomainRegistry.getCustomObjectSerializer().serialize(event);
        this.timestamp = event.getTimestamp();
        this.name = event.getName();
        this.internal = event.getInternal();
        this.topic = event.getTopic();
        this.applicationId = CommonDomainRegistry.getApplicationInfoService().getApplicationId();
        if (event.getDomainId() != null) {
            this.domainId = event.getDomainId().getDomainId();
        }
        this.traceId = MDC.get(TRACE_ID_LOG);
    }

    /**
     * create stored event with no id, so it will skip mark as sent step.
     * <p>
     * event are deserialized and serialized as StoredEvent
     * <p>
     * event like app start must be converted to stored event first
     *
     * @param event domain event
     * @return skip stored event
     */
    public static StoredEvent skipStoredEvent(DomainEvent event) {
        StoredEvent storedEvent = new StoredEvent(event);
        storedEvent.id = null;//set id to null
        return storedEvent;
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
