package com.mt.common.domain.model.restful.query;

import lombok.AccessLevel;
import lombok.Setter;

public abstract class QueryCriteria {
    @Setter(AccessLevel.PROTECTED)
    protected PageConfig pageConfig;
    @Setter(AccessLevel.PROTECTED)
    protected QueryConfig queryConfig;

    public <T extends QueryCriteria> T pageOf(int a) {
        PageConfig pageConfig = this.pageConfig.pageOf(((Integer) a).longValue());
        setPageConfig(pageConfig);
        return (T) this;
    }

    boolean count() {
        return queryConfig.count();
    }

    public PageConfig getPageConfig() {
        return this.pageConfig;
    }
}
