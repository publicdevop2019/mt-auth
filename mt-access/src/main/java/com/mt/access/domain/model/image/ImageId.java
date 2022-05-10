package com.mt.access.domain.model.image;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import com.mt.common.domain.model.domain_id.GeneratedDomainId;
import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class ImageId extends GeneratedDomainId implements Serializable {
    public ImageId() {
        super();
    }

    public ImageId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0I";
    }
}
