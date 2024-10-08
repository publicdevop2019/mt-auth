package com.mt.access.domain.model.user;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Arrays;
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
public class UserQuery extends QueryCriteria {
    private static final String EMAIL = "email";
    private static final String MOBILE = "mobile";
    private static final String USERNAME = "username";
    private static final String ID = "id";
    private static final String PROJECT_IDS = "projectIds";
    private Set<String> userEmailKeys;
    private Set<String> userMobileKeys;
    private Set<String> usernameKeys;
    private Set<UserId> userIds;
    private Set<ProjectId> projectIds;
    private UserSort userSort;

    public UserQuery(UserId userId) {
        this.userIds = new HashSet<>(List.of(userId));
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setUserSort(pageConfig);
    }

    public UserQuery(Set<UserId> userIds) {
        this.userIds = userIds;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setUserSort(pageConfig);
    }

    public UserQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(new QueryConfig(config));
        setUserSort(pageConfig);
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
            EMAIL, MOBILE, USERNAME, ID, PROJECT_IDS);
        Optional.ofNullable(stringStringMap.get(EMAIL)).ifPresent(e -> {
            userEmailKeys = new HashSet<>(List.of(e.split("\\.")));
        });
        Optional.ofNullable(stringStringMap.get(MOBILE)).ifPresent(e -> {
            userMobileKeys = new HashSet<>(List.of(e.split("\\.")));
        });
        Optional.ofNullable(stringStringMap.get(USERNAME)).ifPresent(e -> {
            usernameKeys = new HashSet<>(List.of(e.split("\\.")));
        });
        Optional.ofNullable(stringStringMap.get(PROJECT_IDS)).ifPresent(e -> {
            projectIds =
                Arrays.stream(e.split("\\.")).map(ProjectId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            userIds = Arrays.stream(e.split("\\.")).map(UserId::new).collect(Collectors.toSet());
        });
    }

    private void setUserSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase(ID)) {
            this.userSort = UserSort.byId(pageConfig.isSortOrderAsc());
        }
        if (pageConfig.getSortBy().equalsIgnoreCase(EMAIL)) {
            this.userSort = UserSort.byEmail(pageConfig.isSortOrderAsc());
        }
        if (pageConfig.getSortBy().equalsIgnoreCase("createAt")) {
            this.userSort = UserSort.byCreateAt(pageConfig.isSortOrderAsc());
        }
        if (pageConfig.getSortBy().equalsIgnoreCase("locked")) {
            this.userSort = UserSort.byLocked(pageConfig.isSortOrderAsc());
        }
    }

    @Getter
    public static class UserSort {
        private final Boolean isAsc;
        private Boolean byEmail;
        private Boolean byId;
        private Boolean byCreateAt;
        private Boolean byLocked;

        public UserSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static UserSort byId(boolean isAsc) {
            UserSort userSort = new UserSort(isAsc);
            userSort.byId = true;
            return userSort;
        }

        public static UserSort byEmail(boolean isAsc) {
            UserSort userSort = new UserSort(isAsc);
            userSort.byEmail = true;
            return userSort;
        }

        public static UserSort byCreateAt(boolean isAsc) {
            UserSort userSort = new UserSort(isAsc);
            userSort.byCreateAt = true;
            return userSort;
        }

        public static UserSort byLocked(boolean isAsc) {
            UserSort userSort = new UserSort(isAsc);
            userSort.byLocked = true;
            return userSort;
        }
    }
}
