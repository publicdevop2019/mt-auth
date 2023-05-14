package com.mt.common.domain.model.restful.query;

import javax.annotation.Nullable;

public class QueryConfig {
    private final String rawValue;

    public static QueryConfig skipCount() {

        return new QueryConfig("sc:1");
    }

    public static QueryConfig countRequired() {

        return new QueryConfig();
    }

    /**
     * create QueryConfig,
     * count total if input is null
     * @param configParam config string
     */
    public QueryConfig(@Nullable String configParam) {
        rawValue = configParam;
    }

    private QueryConfig() {
        rawValue = null;
    }

    public boolean count() {
        return rawValue == null || rawValue.contains("sc:1");
    }
}
