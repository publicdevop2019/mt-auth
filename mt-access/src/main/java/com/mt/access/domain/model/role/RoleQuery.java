package com.mt.access.domain.model.role;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class RoleQuery extends QueryCriteria {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PARENT_ID = "parentId";
    private static final String PROJECT_ID = "projectIds";
    private final RoleSort sort;
    private Set<RoleId> ids;
    private RoleId parentId;
    private Set<ProjectId> projectIds;
    private Set<String> names;

    public RoleQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, ID, NAME,PARENT_ID,PROJECT_ID);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> ids = Arrays.stream(e.split("\\.")).map(RoleId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(NAME)).ifPresent(e -> names = Arrays.stream(e.split("\\.")).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(PARENT_ID)).ifPresent(e -> parentId = new RoleId(e));
        Optional.ofNullable(stringStringMap.get(PROJECT_ID)).ifPresent(e -> projectIds = Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet()));
        setPageConfig(PageConfig.limited(pageParam, 1000));
        setQueryConfig(new QueryConfig(config));
        this.sort = RoleSort.byId(true);
    }

    public RoleQuery(RoleId projectId) {
        ids = new HashSet<>();
        ids.add(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = RoleSort.byId(true);
    }

    @Getter
    public static class RoleSort {
        private final boolean isAsc;
        private boolean byId;

        public RoleSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static RoleSort byId(boolean isAsc) {
            RoleSort userSort = new RoleSort(isAsc);
            userSort.byId = true;
            return userSort;
        }
    }
}
