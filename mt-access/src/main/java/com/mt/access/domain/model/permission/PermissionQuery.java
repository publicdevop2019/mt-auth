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
    private static final String TYPES = "types";
    private PermissionSort sort;
    private Set<PermissionId> ids;
    private Set<ProjectId> projectIds;
    @Setter
    private Set<ProjectId> tenantIds;
    private PermissionId parentId;
    @Setter
    private Set<String> names;
    private Set<PermissionType> types;
    private boolean typesIsAndRelation;
    private Boolean shared;

    public PermissionQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
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

    public PermissionQuery(Set<PermissionId> permissionId) {
        ids = permissionId;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = PermissionSort.byId(true);
    }

    public PermissionQuery(ProjectId projectId, String name) {
        projectIds = new HashSet<>();
        projectIds.add(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = PermissionSort.byId(true);
        this.names = Collections.singleton(name);
    }

    public PermissionQuery(PermissionId permissionId, ProjectId projectId) {
        projectIds = Collections.singleton(projectId);
        ids = Collections.singleton(permissionId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = PermissionSort.byId(true);
    }

    public static PermissionQuery uiPermissionQuery(Set<ProjectId> projectIds, Set<String> names) {
        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.tenantIds = projectIds;
        permissionQuery.setPageConfig(PageConfig.defaultConfig());
        permissionQuery.setQueryConfig(QueryConfig.skipCount());
        permissionQuery.sort = PermissionSort.byId(true);
        permissionQuery.names = names;
        return permissionQuery;
    }

    //create query to find read project permission for tenant
    public static PermissionQuery ofProjectWithTenantIds(ProjectId projectId, Set<ProjectId> ids) {
        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.projectIds = Collections.singleton(projectId);
        permissionQuery.tenantIds = ids;
        permissionQuery.setPageConfig(PageConfig.defaultConfig());
        permissionQuery.setQueryConfig(QueryConfig.skipCount());
        permissionQuery.sort = PermissionSort.byId(true);
        return permissionQuery;
    }

    public static PermissionQuery sharedQuery(String queryParam, String pageParam) {
        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.updateQueryParam(queryParam);
        permissionQuery.setPageConfig(PageConfig.limited(pageParam, 50));
        permissionQuery.setQueryConfig(QueryConfig.countRequired());
        permissionQuery.sort = PermissionSort.byId(true);
        permissionQuery.shared = true;
        return permissionQuery;
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, ID, NAME, PARENT_ID_LITERAL, PROJECT_IDS, TYPES);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> ids = Arrays.stream(e.split("\\.")).map(PermissionId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(NAME)).ifPresent(e -> names = Arrays.stream(e.split("\\.")).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(PARENT_ID_LITERAL)).ifPresent(e -> parentId = new PermissionId((e)));
        Optional.ofNullable(stringStringMap.get(PROJECT_IDS)).ifPresent(e -> projectIds = Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(TYPES)).ifPresent(e ->
        {
            if (e.contains(".")) {
                this.typesIsAndRelation = false;
                types = Arrays.stream(e.split("\\.")).map(ee -> {
                    String s = ee.toUpperCase();
                    return PermissionType.valueOf(s);
                }).collect(Collectors.toSet());

            } else {
                this.typesIsAndRelation = true;
                types = Arrays.stream(e.split("\\$")).map(ee -> {
                    String s = ee.toUpperCase();
                    return PermissionType.valueOf(s);
                }).collect(Collectors.toSet());
            }
        });
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
