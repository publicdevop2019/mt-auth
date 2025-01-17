package com.mt.access.domain.model.permission;

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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class PermissionQuery extends QueryCriteria {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TYPES = "types";
    private Set<PermissionId> ids;

    private Set<ProjectId> projectIds;
    @Setter
    private Set<ProjectId> tenantIds;
    @Setter
    private Set<String> names;
    private Set<PermissionType> types;
    private Boolean shared;

    public PermissionQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageParam, 1000));
        setQueryConfig(new QueryConfig(config));
    }

    public static PermissionQuery internalQuery(Set<PermissionId> permissionIds) {
        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.setIds(permissionIds);
        permissionQuery.setPageConfig(PageConfig.defaultConfig());
        permissionQuery.setQueryConfig(QueryConfig.countRequired());
        return permissionQuery;
    }

    public static PermissionQuery internalQuery(Set<PermissionId> permissionIds,
                                                PermissionType type) {
        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.setIds(permissionIds);
        permissionQuery.types = Collections.singleton(type);
        permissionQuery.setPageConfig(PageConfig.defaultConfig());
        permissionQuery.setQueryConfig(QueryConfig.countRequired());
        return permissionQuery;
    }

    public static PermissionQuery uiPermissionQuery(ProjectId projectId, Set<String> names) {
        Validator.notNull(projectId);
        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.tenantIds = Collections.singleton(projectId);
        permissionQuery.setPageConfig(
            PageConfig.limited("num:0,size:" + names.size(), names.size()));
        permissionQuery.setQueryConfig(QueryConfig.skipCount());
        permissionQuery.names = names;
        return permissionQuery;
    }

    /**
     * create query to find tenant permission for root project
     *
     * @param tenantIds tenant id
     * @return PermissionQuery
     */
    public static PermissionQuery ofProjectWithTenantIds(
        Set<ProjectId> tenantIds,
        String permissionName
    ) {
        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.projectIds =
            Collections.singleton(new ProjectId(AppConstant.MAIN_PROJECT_ID));
        Validator.notEmpty(tenantIds);
        permissionQuery.tenantIds = tenantIds;
        permissionQuery.setPageConfig(PageConfig.defaultConfig());
        permissionQuery.setQueryConfig(QueryConfig.countRequired());
        permissionQuery.names = Collections.singleton(permissionName);
        return permissionQuery;
    }

    /**
     * query permission that is related to shared endpoints and subscribed by user
     *
     * @param subPermissionIds subscribed permission ids
     * @param queryParam       query param
     * @param pageParam        page param
     * @return PermissionQuery
     */
    public static PermissionQuery subscribeSharedQuery(
        Set<PermissionId> subPermissionIds,
        String queryParam, String pageParam) {
        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.updateQueryParam(queryParam);
        permissionQuery.setIds(subPermissionIds);
        permissionQuery.setPageConfig(PageConfig.limited(pageParam, 50));
        permissionQuery.setQueryConfig(QueryConfig.countRequired());
        permissionQuery.shared = true;
        return permissionQuery;
    }

    public static PermissionQuery tenantQuery(ProjectId tenantId, PermissionId permissionId) {
        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.projectIds = Collections.singleton(tenantId);
        permissionQuery.setIds(Collections.singleton(permissionId));
        permissionQuery.setPageConfig(PageConfig.defaultConfig());
        permissionQuery.setQueryConfig(QueryConfig.skipCount());
        return permissionQuery;
    }

    private void setIds(Set<PermissionId> ids) {
        Validator.noNullMember(ids);
        this.ids = ids;
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap =
            QueryUtility.parseQuery(queryParam, ID, NAME, AppConstant.QUERY_PROJECT_IDS, TYPES);
        Optional.ofNullable(stringStringMap.get(ID))
            .ifPresent(e -> setIds(
                Arrays.stream(e.split("\\.")).map(PermissionId::new)
                    .collect(Collectors.toSet())));
        Optional.ofNullable(stringStringMap.get(NAME))
            .ifPresent(e -> names = Arrays.stream(e.split("\\."))
                .collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(AppConstant.QUERY_PROJECT_IDS))
            .ifPresent(e -> projectIds =
                Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(TYPES)).ifPresent(e -> {
            if (e.contains(".")) {
                types = Arrays.stream(e.split("\\.")).map(ee -> {
                    String s = ee.toUpperCase();
                    return PermissionType.valueOf(s);
                }).collect(Collectors.toSet());
            } else {
                types = Collections.singleton(PermissionType.valueOf(e.toUpperCase()));
            }
        });
    }
}
