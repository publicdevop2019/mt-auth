package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PROTECTED)
//cannot be abstract due to serialization issue
public class DomainEvent implements Serializable {

    private Long id;

    private Long timestamp;
    private String name = this.getClass().getName();

    private DomainId domainId;

    private Set<DomainId> domainIds;
    private boolean internal = true;
    private String topic;

    public DomainEvent(DomainId domainId) {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setTimestamp(new Date().getTime());
        setDomainId(domainId);
    }

    public DomainEvent(Set<DomainId> domainIds) {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setTimestamp(new Date().getTime());
        setDomainIds(domainIds);
    }

    public DomainEvent() {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setTimestamp(new Date().getTime());
    }
}
