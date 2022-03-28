package com.mt.access.domain.model.cache_profile;

import com.mt.common.domain.model.sql.converter.EnumSetConverter;

/**
 * http cache control value.
 */
public enum CacheControlValue {

    must_revalidate("must-revalidate"),
    no_cache("no-cache"),
    no_store("no-store"),
    no_transform("no-transform"),
    _public("public"),
    _private("private"),
    proxy_revalidate("proxy-revalidate"),
    max_age("max-age"),
    s_maxage("s-maxage");
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
        throw new IllegalArgumentException("unknown enum label value");
    }

    /**
     * common database converter.
     */
    public static class DbConverter extends EnumSetConverter<CacheControlValue> {
        public DbConverter() {
            super(CacheControlValue.class);
        }
    }
}
