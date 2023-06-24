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
    private static final String EMAIL_LIKE = "emailLike";
    @Getter
    private Sort sort;
    @Getter
    private String emailLike;
    @Getter
    private Set<UserId> userIds;
    @Getter
    private RoleId roleId;
    @Getter
    private Set<ProjectId> projectIds;

    private UserRelationQuery() {
    }

    public UserRelationQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap =
            QueryUtility.parseQuery(queryParam, USER_ID, PROJECT_ID, EMAIL_LIKE);
        Optional.ofNullable(stringStringMap.get(USER_ID)).ifPresent(e -> userIds =
            Arrays.stream(e.split("\\.")).map(UserId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(PROJECT_ID)).ifPresent(e -> projectIds =
            Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(EMAIL_LIKE)).ifPresent(e -> emailLike = e);
        setPageConfig(PageConfig.limited(pageParam, 1000));
        setQueryConfig(new QueryConfig(config));
        this.sort = Sort.byId(true);
    }

    public UserRelationQuery(UserId userId, ProjectId projectId) {
        userIds = new HashSet<>();
        userIds.add(userId);
        projectIds = new HashSet<>();
        projectIds.add(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = Sort.byId(true);
    }

    public UserRelationQuery(UserId userId) {
        userIds = new HashSet<>();
        userIds.add(userId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = Sort.byId(true);
    }

    public static UserRelationQuery findTenantAdmin(RoleId tenantAdminRole, String pageConfig) {
        UserRelationQuery userRelationQuery = new UserRelationQuery();
        userRelationQuery.projectIds = Collections.singleton(new ProjectId(MT_AUTH_PROJECT_ID));
        userRelationQuery.roleId = tenantAdminRole;
        userRelationQuery.pageConfig = PageConfig.limited(pageConfig, 1000);
        userRelationQuery.queryConfig = QueryConfig.countRequired();
        userRelationQuery.sort = Sort.byId(true);
        return userRelationQuery;
    }

    @Getter
    public static class Sort {
        private final Boolean isAsc;
        private Boolean byId;

        public Sort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static Sort byId(boolean isAsc) {
            Sort userSort = new Sort(isAsc);
            userSort.byId = true;
            return userSort;
        }
    }
}
