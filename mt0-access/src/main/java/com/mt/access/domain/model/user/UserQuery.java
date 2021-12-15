package com.mt.access.domain.model.user;

import com.mt.access.domain.model.system_role.SystemRoleId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class UserQuery extends QueryCriteria {
    public static final String EMAIL = "email";
    public static final String ID = "id";
    public static final String SUBSCRIPTION = "subscription";
    public static final String GRANTED_AUTHORITIES = "grantedAuthorities";
    private Set<String> userEmails;
    private Set<UserId> userIds;
    private Boolean subscription;
    private SystemRoleId authoritiesSearch;
    private UserSort userSort;

    public UserQuery(UserId userId) {
        this.userIds = new HashSet<>(List.of(userId));
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

    public UserQuery(SystemRoleId systemRoleId) {
        this.authoritiesSearch=systemRoleId;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setUserSort(pageConfig);
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,
                EMAIL,ID,SUBSCRIPTION,GRANTED_AUTHORITIES);
        Optional.ofNullable(stringStringMap.get(EMAIL)).ifPresent(e -> {
            userEmails = new HashSet<>(List.of(e.split("\\.")));
        });
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            userIds = Arrays.stream(e.split("\\.")).map(UserId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(SUBSCRIPTION)).ifPresent(e -> subscription = e.equalsIgnoreCase("1"));
        Optional.ofNullable(stringStringMap.get(GRANTED_AUTHORITIES)).ifPresent(e -> authoritiesSearch = new SystemRoleId(e));
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
        private boolean byEmail;
        private boolean byId;
        private boolean byCreateAt;
        private boolean byLocked;
        private final boolean isAsc;

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
