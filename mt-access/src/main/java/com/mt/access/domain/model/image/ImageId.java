package com.mt.access.domain.model.image;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import javax.persistence.Embeddable;

@Embeddable
public class ImageId extends DomainId {
    public ImageId() {
        super();
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0I" + s.toUpperCase());
    }

    public ImageId(String domainId) {
        super(domainId);
    }

    @Override
    public String toString() {
        return getDomainId();
    }
}
