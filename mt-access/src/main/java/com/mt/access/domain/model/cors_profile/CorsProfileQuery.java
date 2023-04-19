package com.mt.access.domain.model.cors_profile;

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
public class CorsProfileQuery extends QueryCriteria {
    private static final String ID = "id";
    private static final String PROJECT_ID = "projectIds";
    private Set<CorsProfileId> ids;
    private ProjectId projectId;
    private CorsProfileSort sort;

    public CorsProfileQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
        Validator.notNull(projectId);
        setPageConfig(PageConfig.limited(pageParam, 40));
        setQueryConfig(new QueryConfig(config));
        setSort(pageConfig);
    }

    public CorsProfileQuery(CorsProfileId id) {
        this.ids = Collections.singleton(id);
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

    public CorsProfileQuery(ProjectId projectId, CorsProfileId corsProfileId) {
        this.projectId = projectId;
        this.ids = Collections.singleton(corsProfileId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setSort(pageConfig);
    }

    private void setSort(PageConfig pageConfig) {
        this.sort = CorsProfileSort.byId(pageConfig.isSortOrderAsc());
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
            ID, PROJECT_ID);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e ->
            ids =
                Arrays.stream(e.split("\\.")).map(CorsProfileId::new)
                    .collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(PROJECT_ID))
            .ifPresent(e -> projectId = new ProjectId(e));
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
