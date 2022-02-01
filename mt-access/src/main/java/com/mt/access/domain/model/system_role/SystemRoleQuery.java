package com.mt.access.domain.model.system_role;

import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.revoke_token.RevokeTokenId;
import com.mt.access.domain.model.user.UserQuery;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
@Getter
public class SystemRoleQuery extends QueryCriteria {
    private static final String TYPE = "type";
    private static final String ID = "id";
    private static final String NAME = "name";
    private Set<SystemRoleId> ids;
    private Set<String> names;
    private RoleType type;
    private SystemRoleSort sort;
    public SystemRoleQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, ID, NAME, TYPE);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> ids = Arrays.stream(e.split("\\.")).map(SystemRoleId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(NAME)).ifPresent(e -> names = Arrays.stream(e.split("\\.")).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(TYPE)).ifPresent(e -> type = RoleType.valueOf(e));
        setPageConfig(PageConfig.limited(pageParam, 1100));
        setQueryConfig(new QueryConfig(config));
        this.sort=SystemRoleSort.byId(true);
    }

    public SystemRoleQuery(SystemRoleId systemRoleId) {
        ids=new HashSet<>();
        ids.add(systemRoleId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort=SystemRoleSort.byId(true);
    }

    public SystemRoleQuery(Set<SystemRoleId> roles) {
        ids=new HashSet<>();
        ids.addAll(roles);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort=SystemRoleSort.byId(true);
    }

    @Getter
    public static class SystemRoleSort {
        private boolean byId;
        private final boolean isAsc;

        public SystemRoleSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static SystemRoleSort byId(boolean isAsc) {
            SystemRoleSort userSort = new SystemRoleSort(isAsc);
            userSort.byId = true;
            return userSort;
        }
    }
}
