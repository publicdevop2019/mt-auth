package com.mt.access.domain.model.permission;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;
@NoArgsConstructor
@Getter
public class PermissionQuery extends QueryCriteria {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PARENT_ID_LITERAL = "parentId";
    private static final String PROJECT_IDS = "projectIds";
    private PermissionSort sort;
    private Set<PermissionId> ids;
    private Set<ProjectId> projectIds;
    @Setter
    private Set<ProjectId> tenantIds;
    private PermissionId parentId;
    private Set<String> names;

    public PermissionQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, ID, NAME,PARENT_ID_LITERAL,PROJECT_IDS);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> ids = Arrays.stream(e.split("\\.")).map(PermissionId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(NAME)).ifPresent(e -> names = Arrays.stream(e.split("\\.")).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(PARENT_ID_LITERAL)).ifPresent(e -> parentId = new PermissionId((e)));
        Optional.ofNullable(stringStringMap.get(PROJECT_IDS)).ifPresent(e -> projectIds = Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet()));
        setPageConfig(PageConfig.limited(pageParam, 1000));
        setQueryConfig(new QueryConfig(config));
        this.sort = PermissionSort.byId(true);
    }

    public PermissionQuery(PermissionId permissionId) {
        ids = new HashSet<>();
        ids.add(permissionId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = PermissionSort.byId(true);
    }
    public PermissionQuery(ProjectId projectId,String name) {
        projectIds = new HashSet<>();
        projectIds.add(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = PermissionSort.byId(true);
        this.names=Collections.singleton(name);
    }
    public static PermissionQuery tenantQuery(ProjectId tenantIds){
        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.setTenantIds(Collections.singleton(tenantIds));
        permissionQuery.setPageConfig(PageConfig.defaultConfig());
        permissionQuery. setQueryConfig(QueryConfig.skipCount());
        permissionQuery.sort = PermissionSort.byId(true);
        return permissionQuery;
    }
    @Getter
    public static class PermissionSort {
        private final boolean isAsc;
        private boolean byId;

        public PermissionSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static PermissionSort byId(boolean isAsc) {
            PermissionSort userSort = new PermissionSort(isAsc);
            userSort.byId = true;
            return userSort;
        }
    }
}
