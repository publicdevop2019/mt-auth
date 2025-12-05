package com.mt.access.domain.model.client;

import static com.mt.access.infrastructure.AppConstant.QUERY_PROJECT_IDS;

import com.mt.access.domain.model.project.ProjectId;
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
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ClientQuery extends QueryCriteria {
    private static final String ID = "id";
    private static final String CLIENT_ID = "clientId";
    private static final String NAME = "name";
    private static final String GRANTED_AUTHORITIES = "grantedAuthorities";
    private static final String SCOPE_ENUMS = "scopeEnums";
    private Set<ClientId> clientIds;
    private Set<ProjectId> projectIds;
    private String name;

    public ClientQuery(ClientId clientId) {
        clientIds = new HashSet<>(List.of(clientId));
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public ClientQuery(ClientId clientId, ProjectId projectId) {
        clientIds = Collections.singleton(clientId);
        projectIds = Collections.singleton(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public ClientQuery(String queryParam, String pageConfig, String queryConfig) {
        setPageConfig(PageConfig.limited(pageConfig, 50));
        setQueryConfig(new QueryConfig(queryConfig));
        updateQueryParam(queryParam);
    }

    public ClientQuery(Set<ClientId> clientIds) {
        this.clientIds = clientIds;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    private ClientQuery() {
    }

    public ClientQuery(ProjectId projectId) {
        projectIds = Collections.singleton(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public static ClientQuery internalQuery(String pagingParam, String configParam) {
        ClientQuery clientQuery = new ClientQuery();
        clientQuery.setPageConfig(PageConfig.limited(pagingParam, 50));
        clientQuery.setQueryConfig(new QueryConfig(configParam));
        return clientQuery;
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
            ID, CLIENT_ID, NAME, GRANTED_AUTHORITIES, SCOPE_ENUMS,
            QUERY_PROJECT_IDS);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            clientIds =
                Arrays.stream(e.split("\\.")).map(ClientId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(QUERY_PROJECT_IDS)).ifPresent(e -> {
            projectIds =
                Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(CLIENT_ID)).ifPresent(e -> {
            clientIds =
                Arrays.stream(e.split("\\.")).map(ClientId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(NAME)).ifPresent(e -> name = e);
    }


}
