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
    private boolean internal;
    private String domainId;

    public StoredEventRepresentation(StoredEvent o) {
        setEventBody(o.getEventBody());
        setId(o.getId());
        setTimestamp(o.getTimestamp());
        setName(ClassUtility.getShortName(o.getName()));
        setInternal(o.isInternal());
        setDomainId(o.getDomainId());
    }
}
