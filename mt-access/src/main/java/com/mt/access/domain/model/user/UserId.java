package com.mt.access.domain.model.user;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class UserId extends DomainId implements Serializable {
    public UserId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0U" + s.toUpperCase());
    }

    public UserId(String domainId) {
        super(domainId);
    }
}
