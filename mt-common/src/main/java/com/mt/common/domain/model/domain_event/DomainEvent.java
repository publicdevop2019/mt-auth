package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
//cannot be abstract due to serialization issue
public class DomainEvent implements Serializable {

    private Long id;

    private Long timestamp;

    private String name;

    private DomainId domainId;

    private Set<DomainId> domainIds;
    private boolean internal = true;
    private String topic;

    public void setDomainId(DomainId domainId) {
        this.domainId = domainId;
    }

    public void setDomainIds(Set<DomainId> domainIds) {
        this.domainIds = domainIds;
    }

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
