package com.mt.access.domain.model.user_relation;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class UserRelationQuery extends QueryCriteria {
    private static final String USER_ID = "userId";
    private static final String PROJECT_ID = "projectId";
    @Getter
    private final Sort sort;
    @Getter
    private Set<UserId> userIds;
    @Getter
    private Set<ProjectId> projectIds;

    public UserRelationQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, USER_ID,PROJECT_ID);
        Optional.ofNullable(stringStringMap.get(USER_ID)).ifPresent(e -> userIds = Arrays.stream(e.split("\\.")).map(UserId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(PROJECT_ID)).ifPresent(e -> projectIds = Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet()));
        setPageConfig(PageConfig.limited(pageParam, 1000));
        setQueryConfig(new QueryConfig(config));
        this.sort = Sort.byId(true);
    }

    public UserRelationQuery(UserId userId,ProjectId projectId) {
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

    @Getter
    public static class Sort {
        private final boolean isAsc;
        private boolean byId;

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
