package com.mt.common.domain.model.job;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import lombok.Getter;

@Getter
public class JobQuery extends QueryCriteria {
    private final JobSort sort = JobSort.byId(true);
    private JobName name;

    public JobQuery() {
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public JobQuery(JobName name) {
        this.name = name;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public static JobQuery all() {
        return new JobQuery();
    }

    @Getter
    public static class JobSort {
        private final boolean isAsc;
        private boolean byId;

        public JobSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static JobSort byId(boolean isAsc) {
            JobSort sort = new JobSort(isAsc);
            sort.byId = true;
            return sort;
        }
    }
}
