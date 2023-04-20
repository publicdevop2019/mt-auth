package com.mt.access.domain.model.cache_profile;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class CacheProfileQuery extends QueryCriteria {
    private static final String ID = "id";
    private static final String PROJECT_ID = "projectIds";
    private Set<CacheProfileId> ids;
    private CacheProfileSort sort;
    private ProjectId projectId;

    public CacheProfileQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
        Validator.notNull(projectId);
        setPageConfig(PageConfig.limited(pageParam, 40));
        setQueryConfig(new QueryConfig(config));
        setSort(pageConfig);
    }

    public CacheProfileQuery(CacheProfileId id) {
        this.ids = Collections.singleton(id);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setSort(pageConfig);
    }

    public CacheProfileQuery(Set<CacheProfileId> collect) {
        this.ids = collect;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setSort(pageConfig);
    }

    public CacheProfileQuery(ProjectId projectId, CacheProfileId cacheProfileId) {
        this.ids = Collections.singleton(cacheProfileId);
        this.projectId = projectId;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setSort(pageConfig);
    }

    private void setSort(PageConfig pageConfig) {
        this.sort = CacheProfileSort.byId(pageConfig.isSortOrderAsc());
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
            ID, PROJECT_ID);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> ids =
            Arrays.stream(e.split("\\.")).map(CacheProfileId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(PROJECT_ID))
            .ifPresent(e -> projectId = new ProjectId(e));
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
