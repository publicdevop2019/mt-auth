package com.mt.access.domain.model.cors_profile;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class CORSProfileQuery extends QueryCriteria {
    private static final String ID = "id";
    private Set<CORSProfileId> ids;
    private CORSProfileSort sort;

    public CORSProfileQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageParam, 40));
        setQueryConfig(new QueryConfig(config));
        setSort(pageConfig);
    }

    public CORSProfileQuery(CORSProfileId id) {
        if (id != null) {
            this.ids = Collections.singleton(id);
        }
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setSort(pageConfig);
    }

    public CORSProfileQuery(Set<CORSProfileId> collect1) {
        this.ids=collect1;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setSort(pageConfig);
    }

    private void setSort(PageConfig pageConfig) {
        this.sort = CORSProfileSort.byId(pageConfig.isSortOrderAsc());
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
                ID);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            ids = Arrays.stream(e.split("\\.")).map(CORSProfileId::new).collect(Collectors.toSet());
        });
    }

    @Getter
    public static class CORSProfileSort {
        private final boolean isAsc;
        private boolean byId;

        public CORSProfileSort(boolean sortOrderAsc) {
            this.isAsc = sortOrderAsc;
        }

        public static CORSProfileSort byId(boolean sortOrderAsc) {
            CORSProfileSort corsProfileSort = new CORSProfileSort(sortOrderAsc);
            corsProfileSort.byId = true;
            return corsProfileSort;
        }
    }
}
