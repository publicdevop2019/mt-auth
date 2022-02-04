package com.mt.access.domain.model.client;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.system_role.SystemRoleId;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.access.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ClientQuery extends QueryCriteria {
    public static final String ID = "id";
    public static final String CLIENT_ID = "clientId";
    public static final String RESOURCE_INDICATOR = "resourceIndicator";
    public static final String NAME = "name";
    public static final String GRANT_TYPE_ENUMS = "grantTypeEnums";
    public static final String GRANTED_AUTHORITIES = "grantedAuthorities";
    public static final String SCOPE_ENUMS = "scopeEnums";
    public static final String RESOURCE_IDS = "resourceIds";
    public static final String ACCESS_TOKEN_VALIDITY_SECONDS = "accessTokenValiditySeconds";
    public static final String PROJECT_ID = "projectIds";
    public static final String CLIENT_TYPE = "types";
    private Set<ClientId> clientIds;
    @Setter(AccessLevel.PRIVATE)
    private Set<ClientId> resources;
    private Set<ProjectId> projectIds;
    private Boolean resourceFlag;
    private String name;
    private String clientTypeSearch;
    private String grantTypeSearch;
    private String accessTokenSecSearch;

    private boolean isInternal = true;

    public ClientQuery(ClientId clientId) {
        clientIds = new HashSet<>(List.of(clientId));
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public static ClientQuery queryByResource(ClientId resourceId){
        ClientQuery clientQuery = new ClientQuery();
        clientQuery.setResources(new HashSet<>(List.of(resourceId)));
        clientQuery.setPageConfig(PageConfig.defaultConfig());
        clientQuery.setQueryConfig(QueryConfig.countRequired());
        return clientQuery;
    }

    public ClientQuery(String queryParam, String pageConfig, String queryConfig, boolean isInternal) {
        this.isInternal = isInternal;
        setPageConfig(PageConfig.limited(pageConfig, 2000));
        setQueryConfig(new QueryConfig(queryConfig));
        updateQueryParam(queryParam);
        validate();
    }

    public ClientQuery(Set<ClientId> clientIds) {
        this.clientIds = clientIds;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public static ClientQuery resourceIds(ClientId removedClientId) {
        ClientQuery clientQuery = new ClientQuery();
        clientQuery.resources = new HashSet<>(List.of(removedClientId));
        clientQuery.setPageConfig(PageConfig.defaultConfig());
        clientQuery.setQueryConfig(QueryConfig.countRequired());
        return clientQuery;
    }

    private ClientQuery() {
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
                ID, CLIENT_ID, RESOURCE_INDICATOR, NAME,
                GRANT_TYPE_ENUMS, GRANTED_AUTHORITIES, SCOPE_ENUMS, RESOURCE_IDS, ACCESS_TOKEN_VALIDITY_SECONDS,PROJECT_ID,CLIENT_TYPE);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            clientIds = Arrays.stream(e.split("\\.")).map(ClientId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(CLIENT_TYPE)).ifPresent(e -> clientTypeSearch = e);
        Optional.ofNullable(stringStringMap.get(PROJECT_ID)).ifPresent(e -> {
            projectIds = Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(CLIENT_ID)).ifPresent(e -> {
            clientIds = Arrays.stream(e.split("\\.")).map(ClientId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(RESOURCE_INDICATOR)).ifPresent(e -> {
            resourceFlag = e.equalsIgnoreCase("1");
        });
        Optional.ofNullable(stringStringMap.get(NAME)).ifPresent(e -> name = e);
        Optional.ofNullable(stringStringMap.get(GRANT_TYPE_ENUMS)).ifPresent(e -> grantTypeSearch = e);
        Optional.ofNullable(stringStringMap.get(RESOURCE_IDS)).ifPresent(e -> {
            resources = Arrays.stream(e.split("\\$")).map(ClientId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(ACCESS_TOKEN_VALIDITY_SECONDS)).ifPresent(e -> accessTokenSecSearch = e);
    }

    private void validate() {
        if (!isInternal) {
            boolean isRoot = DomainRegistry.getAuthenticationService().isUser()
                    && DomainRegistry.getAuthenticationService().userInRole(new SystemRoleId(AppConstant.MT_AUTH_ADMIN_ROLE));
            boolean isUser = DomainRegistry.getAuthenticationService().isUser()
                    && DomainRegistry.getAuthenticationService().userInRole(new SystemRoleId(AppConstant.MT_AUTH_DEFAULT_USER_ROLE));
            if (isRoot || isUser) {
                if (!isRoot) {
                    if (clientIds == null)
                        throw new IllegalArgumentException("only root role allows empty query");
                    if (resources != null
                            || resourceFlag != null
                            || name != null
                            || clientTypeSearch != null
                            || grantTypeSearch != null
                            || accessTokenSecSearch != null)
                        throw new IllegalArgumentException("user role can only query by id");
                }
            } else {
                throw new IllegalArgumentException("only root/user role allows empty query");
            }
        }
    }

}
