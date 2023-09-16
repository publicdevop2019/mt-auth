package com.mt.access.domain.model.cache_profile;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CacheProfileQuery extends QueryCriteria {
    private static final String PROJECT_ID = "projectIds";
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
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, PROJECT_ID);
        Optional.ofNullable(stringStringMap.get(PROJECT_ID))
            .ifPresent(e -> projectId = new ProjectId(e));
        Validator.notNull(projectId);
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
