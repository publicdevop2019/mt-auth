package com.mt.access.domain.model.endpoint;


import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.cors_profile.CORSProfileId;
import com.mt.access.domain.model.system_role.SystemRoleId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class EndpointQuery extends QueryCriteria {
    public static final String ID = "id";
    public static final String RESOURCE_ID = "resourceId";
    public static final String PATH = "path";
    public static final String METHOD = "method";
    private Set<EndpointId> endpointIds;
    private Set<ClientId> clientIds;
    private Set<CORSProfileId> corsProfileIds;
    private String path;
    private String method;
    private EndpointSort endpointSort;
    private Set<SystemRoleId> systemRoleIds;
    private Set<CacheProfileId> cacheProfileIds;

    public EndpointQuery(String queryParam) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
        setEndpointSort(pageConfig);
    }

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
        setPageConfig(PageConfig.limited(pageParam, 40));
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

    public EndpointQuery(SystemRoleId systemRoleId) {
        this.systemRoleIds = Collections.singleton(systemRoleId);
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

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
                ID, RESOURCE_ID, PATH, METHOD);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            endpointIds = Arrays.stream(e.split("\\.")).map(EndpointId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(RESOURCE_ID)).ifPresent(e -> {
            clientIds = Arrays.stream(e.split("\\.")).map(ClientId::new).collect(Collectors.toSet());
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
