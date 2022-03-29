package com.mt.access.domain.model.cors_profile;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class CorsProfileQuery extends QueryCriteria {
    private static final String ID = "id";
    private Set<CorsProfileId> ids;
    private CorsProfileSort sort;

    public CorsProfileQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageParam, 40));
        setQueryConfig(new QueryConfig(config));
        setSort(pageConfig);
    }

    public CorsProfileQuery(CorsProfileId id) {
        if (id != null) {
            this.ids = Collections.singleton(id);
        }
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setSort(pageConfig);
    }

    public CorsProfileQuery(Set<CorsProfileId> collect1) {
        this.ids = collect1;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setSort(pageConfig);
    }

    private void setSort(PageConfig pageConfig) {
        this.sort = CorsProfileSort.byId(pageConfig.isSortOrderAsc());
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
            ID);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            ids = Arrays.stream(e.split("\\.")).map(CorsProfileId::new).collect(Collectors.toSet());
        });
    }

    @Getter
    public static class CorsProfileSort {
        private final boolean isAsc;
        private boolean byId;

        public CorsProfileSort(boolean sortOrderAsc) {
            this.isAsc = sortOrderAsc;
        }

        public static CorsProfileSort byId(boolean sortOrderAsc) {
            CorsProfileSort corsProfileSort = new CorsProfileSort(sortOrderAsc);
            corsProfileSort.byId = true;
            return corsProfileSort;
        }
    }
}
