package com.mt.common.application.domain_event;

import com.mt.common.domain.model.clazz.ClassUtility;
import com.mt.common.domain.model.domain_event.StoredEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoredEventRepresentation {
    private Long id;
    private String eventBody;
    private Long timestamp;
    private String name;
    private Boolean internal;
    private String domainId;

    public StoredEventRepresentation(StoredEvent event) {
        setEventBody(event.getEventBody());
        setId(event.getId());
        setTimestamp(event.getTimestamp());
        setName(ClassUtility.getShortName(event.getName()));
        setInternal(event.getInternal());
        setDomainId(event.getDomainId());
    }
}
