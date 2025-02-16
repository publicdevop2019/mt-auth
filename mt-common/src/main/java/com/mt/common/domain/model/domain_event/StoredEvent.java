package com.mt.common.domain.model.domain_event;

import static com.mt.common.domain.model.constant.AppInfo.TRACE_ID_LOG;

import com.mt.common.domain.CommonDomainRegistry;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.MDC;

@Getter
@NoArgsConstructor
@Data
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode
public class StoredEvent implements Serializable {
    private String eventBody;
    private Long id;
    private Long timestamp;
    private String name;
    private Boolean send = false;
    private String topic;
    private String domainId;

    private String traceId;
    private Boolean routable = true;
    private Boolean rejected = false;

    public StoredEvent(DomainEvent event) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.eventBody = CommonDomainRegistry.getCustomObjectSerializer().serialize(event);
        this.timestamp = event.getTimestamp();
        this.name = event.getName();
        this.topic = event.getTopic();
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

    public static StoredEvent fromDatabaseRow(Long id, String domainId, String eventBody,
                                              String name, Long timestamp,
                                              String topic, Boolean send, Boolean routable,
                                              Boolean rejected, String traceId) {
        StoredEvent storedEvent = new StoredEvent();
        storedEvent.setId(id);
        storedEvent.setDomainId(domainId);
        storedEvent.setEventBody(eventBody);
        storedEvent.setName(name);
        storedEvent.setTimestamp(timestamp);
        storedEvent.setTopic(topic);
        storedEvent.setSend(send);
        storedEvent.setRoutable(routable);
        storedEvent.setRejected(rejected);
        storedEvent.setTraceId(traceId);
        return storedEvent;
    }

    public void sendToMQ() {
        StoredEvent storedEvent =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);

        storedEvent.send = true;

        CommonDomainRegistry.getDomainEventRepository().update(this, storedEvent);
    }

    public void markAsUnroutable() {
        StoredEvent storedEvent =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        storedEvent.routable = false;
        CommonDomainRegistry.getDomainEventRepository().update(this, storedEvent);
    }

    public void markAsRejected() {
        StoredEvent storedEvent =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        storedEvent.rejected = true;
        CommonDomainRegistry.getDomainEventRepository().update(this, storedEvent);
    }
}
