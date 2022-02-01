package com.mt.access.domain.model.project;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
@Getter
public class ProjectQuery extends QueryCriteria {
    private static final String ID = "id";
    private static final String NAME = "name";
    private final ProjectSort sort;
    private Set<ProjectId> ids;
    private Set<String> names;

    public ProjectQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, ID, NAME);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> ids = Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(NAME)).ifPresent(e -> names = Arrays.stream(e.split("\\.")).collect(Collectors.toSet()));
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(new QueryConfig(config));
        this.sort = ProjectSort.byId(true);
    }

    public ProjectQuery(ProjectId projectId) {
        ids = new HashSet<>();
        ids.add(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = ProjectSort.byId(true);
    }

    @Getter
    public static class ProjectSort {
        private final boolean isAsc;
        private boolean byId;

        public ProjectSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static ProjectSort byId(boolean isAsc) {
            ProjectSort userSort = new ProjectSort(isAsc);
            userSort.byId = true;
            return userSort;
        }
    }
}
