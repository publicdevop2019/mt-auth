package com.mt.access.domain.model.cache_profile;

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
public class CacheProfileQuery extends QueryCriteria {
    private Set<CacheProfileId> ids;
    private ProjectId projectId;

    public static CacheProfileQuery tenantQuery(String queryParam, String pageParam,
                                                String config) {
        return new CacheProfileQuery(queryParam, pageParam, config);
    }

    public static CacheProfileQuery tenantQuery(ProjectId projectId,
                                                CacheProfileId cacheProfileId) {
        return new CacheProfileQuery(projectId, cacheProfileId);
    }

    public static CacheProfileQuery internalQuery(Set<CacheProfileId> ids) {
        return new CacheProfileQuery(ids);
    }

    public static CacheProfileQuery internalQuery(CacheProfileId id) {
        return new CacheProfileQuery(Collections.singleton(id));
    }

    private CacheProfileQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap =
            QueryUtility.parseQuery(queryParam, AppConstant.QUERY_PROJECT_IDS,
                AppConstant.QUERY_ID);
        Optional.ofNullable(stringStringMap.get(AppConstant.QUERY_PROJECT_IDS))
            .ifPresent(e -> projectId = new ProjectId(e));
        Validator.notNull(projectId);
        Optional.ofNullable(stringStringMap.get(AppConstant.QUERY_ID)).ifPresent(e -> ids =
            Arrays.stream(e.split("\\.")).map(CacheProfileId::new).collect(Collectors.toSet()));
        setPageConfig(PageConfig.limited(pageParam, 40));
        setQueryConfig(new QueryConfig(config));
    }

    private CacheProfileQuery(ProjectId projectId, CacheProfileId cacheProfileId) {
        Validator.notNull(cacheProfileId);
        Validator.notNull(projectId);
        this.ids = Collections.singleton(cacheProfileId);
        this.projectId = projectId;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    private CacheProfileQuery(Set<CacheProfileId> ids) {
        Validator.notNull(ids);
        Validator.noNullMember(ids);
        this.ids = ids;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }
}
