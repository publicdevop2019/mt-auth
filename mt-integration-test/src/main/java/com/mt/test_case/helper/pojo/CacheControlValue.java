package com.mt.test_case.helper.pojo;

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

}
