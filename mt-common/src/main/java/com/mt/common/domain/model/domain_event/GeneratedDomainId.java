package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.io.Serializable;

/**
 * Generated domain id,
 * format: domain code (app number + first domain letter) + unique long radix value
 * e.g 0U8HPG93IED3
 */
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
            throw new DefinedRuntimeException("wrong domain id prefix: " + raw, "0012",
                HttpResponseCode.BAD_REQUEST);
        }
        String substring = raw.substring(1);
        try {
            Long.parseLong(substring, 36);
        } catch (NumberFormatException ex) {
            throw new DefinedRuntimeException("given domain id is not valid", "0013",
                HttpResponseCode.BAD_REQUEST, ex);
        }
        setDomainId(raw);
    }

    protected abstract String getPrefix();
}
