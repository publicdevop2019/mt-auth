package com.mt.access.domain.model.role;

import static com.mt.access.domain.model.role.Role.PROJECT_ADMIN;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class RoleQuery extends QueryCriteria {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PARENT_ID = "parentId";
    private static final String TYPES = "types";
    private Set<RoleId> ids;
    private RoleId parentId;
    private Boolean parentIdNull;
    private Set<ProjectId> projectIds;
    private Set<String> names;
    private Set<ProjectId> tenantIds;
    private Set<RoleType> types;

    public RoleQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap =
            QueryUtility.parseQuery(queryParam, ID, NAME, PARENT_ID, AppConstant.QUERY_PROJECT_IDS,
                TYPES);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(
            e -> ids = Arrays.stream(e.split("\\.")).map(RoleId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(NAME))
            .ifPresent(e -> names = Arrays.stream(e.split("\\.")).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(PARENT_ID))
            .ifPresent(e -> {
                if ("null".equalsIgnoreCase(e)) {
                    parentIdNull = true;
                } else {
                    parentId = new RoleId(e);
                }
            });
        Optional.ofNullable(stringStringMap.get(AppConstant.QUERY_PROJECT_IDS))
            .ifPresent(e -> projectIds =
                Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(TYPES)).ifPresent(e -> {
            if (e.contains(".")) {
                types = Arrays.stream(e.split("\\.")).map(ee -> {
                    String s = ee.toUpperCase();
                    return RoleType.valueOf(s);
                }).collect(Collectors.toSet());
            } else {
                types = Collections.singleton(RoleType.valueOf(e.toUpperCase()));
            }
        });
        setPageConfig(PageConfig.limited(pageParam, 1000));
        setQueryConfig(new QueryConfig(config));
    }

    public RoleQuery(Set<RoleId> roleIds) {
        ids = roleIds;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public RoleQuery(RoleId roleId, ProjectId projectId) {
        ids = Collections.singleton(roleId);
        projectIds = Collections.singleton(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public RoleQuery(ProjectId projectId, String roleName) {
        names = Collections.singleton(roleName);
        projectIds = Collections.singleton(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public RoleQuery(ProjectId projectId) {
        projectIds = Collections.singleton(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public RoleQuery(RoleType type) {
        this.types = Collections.singleton(type);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public static RoleQuery projectDefaultRoleQuery() {
        RoleQuery roleQuery = new RoleQuery();
        Set<RoleType> roleTypes = new HashSet<>();
        roleTypes.add(RoleType.PROJECT);
        roleTypes.add(RoleType.CLIENT_ROOT);
        roleQuery.types = roleTypes;
        roleQuery.setPageConfig(PageConfig.defaultConfig());
        roleQuery.setQueryConfig(QueryConfig.skipCount());
        return roleQuery;
    }

    public static RoleQuery forClientId(ClientId clientId) {
        RoleQuery roleQuery = new RoleQuery();
        Set<RoleType> roleTypes = new HashSet<>();
        roleTypes.add(RoleType.CLIENT);
        roleQuery.types = roleTypes;
        roleQuery.names = Collections.singleton(clientId.getDomainId());
        roleQuery.setPageConfig(PageConfig.defaultConfig());
        roleQuery.setQueryConfig(QueryConfig.skipCount());
        return roleQuery;
    }

    public static RoleQuery all() {
        RoleQuery roleQuery = new RoleQuery();
        roleQuery.setPageConfig(PageConfig.defaultConfig());
        roleQuery.setQueryConfig(QueryConfig.skipCount());
        return roleQuery;
    }

    public static RoleQuery tenantAdmin(ProjectId tenantProjectId) {
        RoleQuery roleQuery = new RoleQuery();
        roleQuery.setPageConfig(PageConfig.defaultConfig());
        roleQuery.setQueryConfig(QueryConfig.skipCount());
        roleQuery.names = Collections.singleton(PROJECT_ADMIN);
        roleQuery.tenantIds = Collections.singleton(tenantProjectId);
        return roleQuery;
    }
}
