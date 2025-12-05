package com.mt.access.domain.model.endpoint;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;

@Getter
public class RouterQuery extends QueryCriteria {
    private Set<RouterId> routerIds;
    private Set<ProjectId> projectIds;

    public RouterQuery(RouterId routerId, ProjectId projectId) {
        routerIds = Collections.singleton(routerId);
        projectIds = Collections.singleton(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public RouterQuery(ProjectId projectId, String pageParam, String config) {
        projectIds = Collections.singleton(projectId);
        setPageConfig(PageConfig.limited(pageParam, 100));
        setQueryConfig(new QueryConfig(config));
    }

    public RouterQuery(Set<RouterId> collect) {
        routerIds = collect;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public RouterQuery(String pageParam, String config) {
        setPageConfig(PageConfig.limited(pageParam, 100));
        setQueryConfig(new QueryConfig(config));
    }
}
