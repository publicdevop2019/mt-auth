package com.mt.access.domain.model.sub_request;

import com.mt.access.domain.model.position.PositionId;
import com.mt.access.domain.model.position.PositionQuery;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
@Getter
public class SubRequestQuery extends QueryCriteria {
    private final Sort sort;
    private Set<SubRequestId> ids;
    public SubRequestQuery(String pageParam) {
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(QueryConfig.countRequired());
        this.sort = Sort.byId(true);
    }

    public SubRequestQuery(SubRequestId id) {
        ids = new HashSet<>();
        ids.add(id);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = Sort.byId(true);
    }

    @Getter
    public static class Sort {
        private final boolean isAsc;
        private boolean byId;

        public Sort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static Sort byId(boolean isAsc) {
            Sort userSort = new Sort(isAsc);
            userSort.byId = true;
            return userSort;
        }
    }
}
