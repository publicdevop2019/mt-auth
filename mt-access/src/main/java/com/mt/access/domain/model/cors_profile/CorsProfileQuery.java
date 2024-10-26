package com.mt.access.domain.model.cors_profile;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.infrastructure.AppConstant;
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
import lombok.ToString;

@Getter
@ToString
public class CorsProfileQuery extends QueryCriteria {
    private static final String ID = "id";
    private Set<CorsProfileId> ids;
    private ProjectId projectId;

    private CorsProfileQuery(Set<CorsProfileId> collect1) {
        this.ids = collect1;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    private CorsProfileQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
        Validator.notNull(projectId);
        setPageConfig(PageConfig.limited(pageParam, 40));
        setQueryConfig(new QueryConfig(config));
    }

    private CorsProfileQuery(ProjectId projectId, CorsProfileId corsProfileId) {
        this.projectId = projectId;
        this.ids = Collections.singleton(corsProfileId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public static CorsProfileQuery internalQuery(Set<CorsProfileId> ids) {
        return new CorsProfileQuery(ids);
    }

    public static CorsProfileQuery tenantQuery(String queryParam, String pageParam, String config) {
        return new CorsProfileQuery(queryParam, pageParam, config);
    }

    public static CorsProfileQuery tenantQuery(ProjectId projectId, CorsProfileId corsProfileId) {
        return new CorsProfileQuery(projectId, corsProfileId);
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap =
            QueryUtility.parseQuery(queryParam, AppConstant.QUERY_PROJECT_IDS,
                AppConstant.QUERY_ID);
        Optional.ofNullable(stringStringMap.get(AppConstant.QUERY_PROJECT_IDS))
            .ifPresent(e -> projectId = new ProjectId(e));
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e ->
            ids =
                Arrays.stream(e.split("\\.")).map(CorsProfileId::new)
                    .collect(Collectors.toSet()));
    }
}
