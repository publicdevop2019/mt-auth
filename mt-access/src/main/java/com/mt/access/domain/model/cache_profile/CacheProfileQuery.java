package com.mt.access.domain.model.cache_profile;

import com.mt.access.domain.model.cors_profile.CORSProfileId;
import com.mt.access.domain.model.cors_profile.CORSProfileQuery;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
@Getter
public class CacheProfileQuery extends QueryCriteria {
    private static final String ID = "id";
    private Set<CacheProfileId> ids;
    private CacheProfileSort sort;
    public CacheProfileQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageParam, 40));
        setQueryConfig(new QueryConfig(config));
        setSort(pageConfig);
    }

    public CacheProfileQuery(CacheProfileId id) {
        this.ids= Collections.singleton(id);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setSort(pageConfig);
    }

    public CacheProfileQuery(Set<CacheProfileId> collect) {
        this.ids= collect;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setSort(pageConfig);
    }

    private void setSort(PageConfig pageConfig) {
        this.sort = CacheProfileSort.byId(pageConfig.isSortOrderAsc());
    }
    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
                ID);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            ids = Arrays.stream(e.split("\\.")).map(CacheProfileId::new).collect(Collectors.toSet());
        });
    }

    @Getter
    public static class CacheProfileSort {
        private final boolean isAsc;
        private boolean byId;

        public CacheProfileSort(boolean sortOrderAsc) {
            this.isAsc = sortOrderAsc;
        }

        public static CacheProfileSort byId(boolean sortOrderAsc) {
            CacheProfileSort cacheProfileSort = new CacheProfileSort(sortOrderAsc);
            cacheProfileSort.byId = true;
            return cacheProfileSort;
        }
    }
}
