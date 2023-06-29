package com.mt.access.domain.model.cache_profile;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;

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
}
