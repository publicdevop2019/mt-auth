package com.mt.access.domain.model.user;

import static com.mt.access.infrastructure.AppConstant.MT_AUTH_PROJECT_ID;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
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
import lombok.Getter;
import lombok.ToString;

@ToString
public class UserRelationQuery extends QueryCriteria {
    private static final String USER_ID = "userId";
    private static final String PROJECT_ID = "projectIds";
    private static final String EMAIL = "email";
    private static final String MOBILE = "mobile";
    private static final String USERNAME = "username";
    @Getter
    private String email;
    @Getter
    private String mobileNumber;
    @Getter
    private String username;
    @Getter
    private Set<UserId> userIds;
    @Getter
    private RoleId roleId;
    @Getter
    private Set<ProjectId> projectIds;

    private UserRelationQuery() {
    }

    public UserRelationQuery(String queryParam, String pageParam, String config) {
        Map<String, String> queryMap =
            QueryUtility.parseQuery(queryParam, USER_ID, PROJECT_ID, EMAIL, MOBILE, USERNAME);
        Optional.ofNullable(queryMap.get(USER_ID)).ifPresent(e -> userIds =
            Arrays.stream(e.split("\\.")).map(UserId::new).collect(Collectors.toSet()));
        Optional.ofNullable(queryMap.get(PROJECT_ID)).ifPresent(e -> projectIds =
            Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet()));
        Optional.ofNullable(queryMap.get(EMAIL)).ifPresent(e -> email = e);
        Optional.ofNullable(queryMap.get(MOBILE)).ifPresent(e -> mobileNumber = e);
        Optional.ofNullable(queryMap.get(USERNAME)).ifPresent(e -> username = e);
        setPageConfig(PageConfig.limited(pageParam, 1000));
        setQueryConfig(new QueryConfig(config));
    }

    public UserRelationQuery(UserId userId) {
        userIds = new HashSet<>();
        userIds.add(userId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public static UserRelationQuery findTenantAdmin(RoleId tenantAdminRole, String pageConfig) {
        UserRelationQuery userRelationQuery = new UserRelationQuery();
        userRelationQuery.projectIds = Collections.singleton(new ProjectId(MT_AUTH_PROJECT_ID));
        userRelationQuery.roleId = tenantAdminRole;
        userRelationQuery.pageConfig = PageConfig.limited(pageConfig, 1000);
        userRelationQuery.queryConfig = QueryConfig.countRequired();
        return userRelationQuery;
    }

    public static UserRelationQuery internalAdminQuery(RoleId adminRole) {
        UserRelationQuery userRelationQuery = new UserRelationQuery();
        userRelationQuery.projectIds = Collections.singleton(new ProjectId(MT_AUTH_PROJECT_ID));
        userRelationQuery.roleId = adminRole;
        userRelationQuery.pageConfig = PageConfig.defaultConfig();
        userRelationQuery.queryConfig = QueryConfig.countRequired();
        return userRelationQuery;
    }
}
