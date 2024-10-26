package com.mt.access.domain.model.endpoint;


import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class EndpointQuery extends QueryCriteria {
    public static final String ID = "id";
    public static final String RESOURCE_ID = "resourceId";
    public static final String PATH = "path";
    public static final String METHOD = "method";
    public static final String PERMISSION_IDS = "permissionId";
    private Set<EndpointId> endpointIds;
    private Set<PermissionId> permissionIds;
    private Set<ClientId> clientIds;
    private Set<ProjectId> projectIds;
    private Set<CorsProfileId> corsProfileIds;
    private String path;
    private String method;
    @Setter(AccessLevel.PRIVATE)
    private Boolean isWebsocket;
    @Setter(AccessLevel.PRIVATE)
    private Boolean isShared;
    @Setter(AccessLevel.PRIVATE)
    private Boolean isSecured;
    private Set<CacheProfileId> cacheProfileIds;

    public EndpointQuery() {
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public EndpointQuery(EndpointId endpointId) {
        endpointIds = new HashSet<>(List.of(endpointId));
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public EndpointQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageParam, 1000));
        setQueryConfig(new QueryConfig(config));
    }

    public EndpointQuery(String pageParam) {
        setPageConfig(PageConfig.limited(pageParam, 1000));
        setQueryConfig(QueryConfig.countRequired());
    }

    public EndpointQuery(ClientId clientId) {
        clientIds = Collections.singleton(clientId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public EndpointQuery(CorsProfileId corsProfileId) {
        corsProfileIds = Collections.singleton(corsProfileId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public EndpointQuery(CacheProfileId profileId) {
        this.cacheProfileIds = Collections.singleton(profileId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public EndpointQuery(Set<EndpointId> endpointIds) {
        this.endpointIds = endpointIds;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public EndpointQuery(EndpointId endpointId, ProjectId projectId) {
        endpointIds = Collections.singleton(endpointId);
        projectIds = Collections.singleton(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public EndpointQuery(String queryParam, ProjectId projectId) {
        updateQueryParam(queryParam);
        projectIds = Collections.singleton(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public static EndpointQuery permissionQuery(Set<PermissionId> externalPermissions) {
        EndpointQuery endpointQuery = new EndpointQuery();
        endpointQuery.permissionIds = externalPermissions;
        endpointQuery.setPageConfig(PageConfig.defaultConfig());
        endpointQuery.setQueryConfig(QueryConfig.countRequired());
        return endpointQuery;
    }

    public static EndpointQuery tenantQuery(Set<EndpointId> endpointIds, Set<ProjectId> tenantIds) {
        EndpointQuery endpointQuery = new EndpointQuery();
        endpointQuery.endpointIds = endpointIds;
        endpointQuery.projectIds = tenantIds;
        endpointQuery.setPageConfig(PageConfig.defaultConfig());
        endpointQuery.setQueryConfig(QueryConfig.countRequired());
        return endpointQuery;
    }

    public static EndpointQuery websocketQuery() {
        EndpointQuery endpointQuery = new EndpointQuery();
        endpointQuery.setIsWebsocket(true);
        return endpointQuery;
    }

    public static EndpointQuery sharedQuery(String queryParam, String pageParam, String config) {
        EndpointQuery endpointQuery = new EndpointQuery(queryParam, pageParam, config);
        endpointQuery.setIsShared(true);
        return endpointQuery;
    }

    public static EndpointQuery securedQuery() {
        EndpointQuery endpointQuery = new EndpointQuery();
        endpointQuery.setIsSecured(true);
        return endpointQuery;
    }

    public static EndpointQuery tenantQueryProtected(String queryParam, String pageParam,
                                                     String config) {
        EndpointQuery endpointQuery = new EndpointQuery(queryParam, pageParam, config);
        endpointQuery.updateQueryParam(queryParam);
        endpointQuery.setIsSecured(true);
        return endpointQuery;
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
            ID, RESOURCE_ID, PATH, METHOD, AppConstant.QUERY_PROJECT_IDS, PERMISSION_IDS);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            endpointIds =
                Arrays.stream(e.split("\\.")).map(EndpointId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(RESOURCE_ID)).ifPresent(e -> {
            clientIds =
                Arrays.stream(e.split("\\.")).map(ClientId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(AppConstant.QUERY_PROJECT_IDS)).ifPresent(e -> {
            projectIds =
                Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(PERMISSION_IDS)).ifPresent(e -> {
            permissionIds =
                Arrays.stream(e.split("\\.")).map(PermissionId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(PATH)).ifPresent(e -> path = e);
        Optional.ofNullable(stringStringMap.get(METHOD)).ifPresent(e -> method = e);
    }
}
