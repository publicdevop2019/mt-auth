package com.mt.common.domain.model.domain_id;

import com.mt.common.domain.CommonDomainRegistry;
import java.io.Serializable;
import javax.persistence.MappedSuperclass;

/**
 * Generated domain id,
 * format: domain code (app number + first domain letter) + unique long radix value
 * e.g 0U8HPG93IED3
 */
@MappedSuperclass
public abstract class GeneratedDomainId extends DomainId implements Serializable {
    protected GeneratedDomainId() {
        super();
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId(getPrefix() + s.toUpperCase());
    }

    protected GeneratedDomainId(String raw) {
        super(raw);
        if (raw.indexOf(getPrefix()) != 0) {
            throw new IllegalArgumentException("wrong domain id prefix");
        }
        String substring = raw.substring(1);
        try {
            Long.parseLong(substring, 36);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("given domain id is not valid");
        }
        setDomainId(raw);
    }

    protected abstract String getPrefix();
}
