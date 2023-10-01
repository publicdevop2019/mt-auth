package com.mt.access.domain.model.image;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
import java.io.Serializable;

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
