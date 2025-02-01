package com.mt.access.domain.model.cache_profile;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.Utility;
import java.util.Set;

/**
 * http cache control value.
 */
public enum CacheControlValue {

    MUST_REVALIDATE("must-revalidate"),
    NO_CACHE("no-cache"),
    NO_STORE("no-store"),
    NO_TRANSFORM("no-transform"),
    PUBLIC("public"),
    PRIVATE("private"),
    PROXY_REVALIDATE("proxy-revalidate"),
    MAX_AGE("max-age"),
    S_MAX_AGE("s-maxage");
    public final String label;

    CacheControlValue(String label) {
        this.label = label;
    }

    /**
     * parse value from string.
     *
     * @param label raw value
     * @return enum
     */
    public static CacheControlValue valueOfLabel(String label) {
        for (CacheControlValue e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        throw new DefinedRuntimeException("unknown cache control value", "1033",
            HttpResponseCode.BAD_REQUEST);
    }

    public static void add(CacheProfile cacheProfile, Set<CacheControlValue> values) {
        Validator.validOptionalCollection(9, values);
        DomainRegistry.getCacheControlRepository().add(cacheProfile, values);
    }

    public static void update(CacheProfile cacheProfile, Set<CacheControlValue> old,
                              Set<CacheControlValue> cacheControl, TransactionContext context) {
        if (!Checker.sameAs(old, cacheControl)) {
            context.append(new CacheProfileUpdated(cacheProfile));
            Validator.validOptionalCollection(9, cacheControl);
            Utility.updateSet(old, cacheControl,
                (added) -> DomainRegistry.getCacheControlRepository().add(cacheProfile, added),
                (removed) -> DomainRegistry.getCacheControlRepository()
                    .remove(cacheProfile, removed));
        }
    }
}
