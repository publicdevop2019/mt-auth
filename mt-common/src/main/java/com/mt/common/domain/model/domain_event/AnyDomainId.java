package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.model.domain_id.DomainId;
import lombok.NoArgsConstructor;

/**
 * domain id used for general purpose, like app started .etc
 */
@NoArgsConstructor
public class AnyDomainId extends DomainId {
    public AnyDomainId(String domainId) {
        super(domainId);
    }
}
