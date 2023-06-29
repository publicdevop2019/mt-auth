package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
@NoArgsConstructor
//cannot be abstract due to serialization issue
public class DomainEvent implements Serializable {

    private Long id;

    private Long timestamp;

    private String name;

    private DomainId domainId;

    private Set<DomainId> domainIds;
    private Boolean internal = true;
    private String topic;

    private void setDomainId(DomainId domainId) {
        this.domainId = domainId;
    }

    /**
     * safe for serialization, set domain id of first domain id set
     * @param domainIds domain id set
     */
    private void setDomainIds(Set<DomainId> domainIds) {
        this.domainIds = domainIds;
        if (domainIds != null) {
            Optional<DomainId> first = domainIds.stream().findFirst();
            first.ifPresent(value -> this.domainId = value);
        }
    }

    public DomainEvent(DomainId domainId) {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setTimestamp(new Date().getTime());
        setDomainId(domainId);
    }

    public DomainEvent(Set<DomainId> domainIds) {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setTimestamp(new Date().getTime());
        Validator.notEmpty(domainIds);//cannot add validator on setter due to serialization
        setDomainIds(domainIds);
    }
}
