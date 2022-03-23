package com.mt.access.domain.model.endpoint;


import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.cors_profile.CORSProfileId;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class EndpointQuery extends QueryCriteria {
    public static final String ID = "id";
    public static final String RESOURCE_ID = "resourceId";
    public static final String PATH = "path";
    public static final String METHOD = "method";
    public static final String PROJECT_IDS = "projectIds";
    public static final String PERMISSION_IDS = "permissionId";
    private Set<EndpointId> endpointIds;
    private Set<PermissionId> permissionIds;
    private Set<ClientId> clientIds;
    private Set<ProjectId> projectIds;
    private Set<CORSProfileId> corsProfileIds;
    private String path;
    private String method;
    @Setter(AccessLevel.PRIVATE)
    private Boolean isWebsocket;
    @Setter(AccessLevel.PRIVATE)
    private Boolean isShared;
    @Setter(AccessLevel.PRIVATE)
    private Boolean isSecured;
    private EndpointSort endpointSort;
    private Set<CacheProfileId> cacheProfileIds;

    public EndpointQuery() {
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
        setEndpointSort(pageConfig);
    }

    public EndpointQuery(EndpointId endpointId) {
        endpointIds = new HashSet<>(List.of(endpointId));
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setEndpointSort(pageConfig);
    }

    public EndpointQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageParam, 1000));
        setQueryConfig(new QueryConfig(config));
        setEndpointSort(pageConfig);
    }

    public EndpointQuery(ClientId domainId) {
        clientIds = Collections.singleton(domainId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
        setEndpointSort(pageConfig);
    }

    public EndpointQuery(CORSProfileId corsProfileId) {
        corsProfileIds = Collections.singleton(corsProfileId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
        setEndpointSort(pageConfig);
    }

    public EndpointQuery(CacheProfileId profileId) {
        this.cacheProfileIds = Collections.singleton(profileId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
        setEndpointSort(pageConfig);
    }

    public EndpointQuery(Set<EndpointId> collect1) {
        endpointIds = collect1;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setEndpointSort(pageConfig);
    }

    public EndpointQuery(EndpointId endpointId, ProjectId projectId) {
        endpointIds = Collections.singleton(endpointId);
        projectIds = Collections.singleton(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setEndpointSort(pageConfig);
    }

    public EndpointQuery(String queryParam, ProjectId projectId) {
        updateQueryParam(queryParam);
        projectIds = Collections.singleton(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
        setEndpointSort(pageConfig);
    }

    public static EndpointQuery permissionQuery(Set<PermissionId> externalPermissions) {
        EndpointQuery endpointQuery = new EndpointQuery();
        endpointQuery.permissionIds = externalPermissions;
        endpointQuery.setPageConfig(PageConfig.defaultConfig());
        endpointQuery.setQueryConfig(QueryConfig.countRequired());
        endpointQuery.setEndpointSort(endpointQuery.pageConfig);
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

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
                ID, RESOURCE_ID, PATH, METHOD, PROJECT_IDS, PERMISSION_IDS);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            endpointIds = Arrays.stream(e.split("\\.")).map(EndpointId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(RESOURCE_ID)).ifPresent(e -> {
            clientIds = Arrays.stream(e.split("\\.")).map(ClientId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(PROJECT_IDS)).ifPresent(e -> {
            projectIds = Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(PERMISSION_IDS)).ifPresent(e -> {
            permissionIds = Arrays.stream(e.split("\\.")).map(PermissionId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(PATH)).ifPresent(e -> path = e);
        Optional.ofNullable(stringStringMap.get(METHOD)).ifPresent(e -> method = e);
    }

    private void setEndpointSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase(ID)) {
            this.endpointSort = EndpointSort.byId(pageConfig.isSortOrderAsc());
        }
        if (pageConfig.getSortBy().equalsIgnoreCase(RESOURCE_ID)) {
            this.endpointSort = EndpointSort.byResourceId(pageConfig.isSortOrderAsc());
        }
        if (pageConfig.getSortBy().equalsIgnoreCase(PATH)) {
            this.endpointSort = EndpointSort.byPath(pageConfig.isSortOrderAsc());
        }
        if (pageConfig.getSortBy().equalsIgnoreCase(METHOD)) {
            this.endpointSort = EndpointSort.byMethod(pageConfig.isSortOrderAsc());
        }
    }

    @Getter
    public static class EndpointSort {
        private final boolean isAsc;
        private boolean byId;
        private boolean byClientId;
        private boolean byMethod;
        private boolean byPath;

        private EndpointSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static EndpointSort byId(boolean isAsc) {
            EndpointSort endpointSort = new EndpointSort(isAsc);
            endpointSort.byId = true;
            return endpointSort;
        }

        public static EndpointSort byResourceId(boolean isAsc) {
            EndpointSort endpointSort = new EndpointSort(isAsc);
            endpointSort.byClientId = true;
            return endpointSort;
        }

        public static EndpointSort byMethod(boolean isAsc) {
            EndpointSort endpointSort = new EndpointSort(isAsc);
            endpointSort.byMethod = true;
            return endpointSort;
        }

        public static EndpointSort byPath(boolean isAsc) {
            EndpointSort endpointSort = new EndpointSort(isAsc);
            endpointSort.byPath = true;
            return endpointSort;
        }
    }
}
