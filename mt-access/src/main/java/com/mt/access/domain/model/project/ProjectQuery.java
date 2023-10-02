package com.mt.access.domain.model.project;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.validate.Validator;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProjectQuery extends QueryCriteria {
    private Set<ProjectId> ids;

    public ProjectQuery(String pageParam, String config) {
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(new QueryConfig(config));
    }

    public ProjectQuery(ProjectId projectId) {
        ids = new HashSet<>();
        ids.add(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public ProjectQuery(Set<ProjectId> tenantIds, String pageParam) {
        Validator.notEmpty(tenantIds);
        this.ids = tenantIds;
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(QueryConfig.skipCount());
    }

    public ProjectQuery(Set<ProjectId> collect) {
        ids = collect;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }
}
