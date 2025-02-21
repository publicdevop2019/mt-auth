package com.mt.common.domain.model.job;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class JobQuery extends QueryCriteria {
    private final JobSort sort = JobSort.byId(true);
    private String name;
    private JobId id;

    private JobQuery() {
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public static JobQuery byName(String name) {
        JobQuery query = new JobQuery();
        query.name = name;
        return query;
    }

    public static JobQuery all() {
        return new JobQuery();
    }

    @Getter
    public static class JobSort {
        private final Boolean isAsc;
        private Boolean byId;

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
