package com.mt.access.domain.model.organization;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class OrganizationQuery extends QueryCriteria {
    private static final String ID = "id";
    private static final String NAME = "name";
    private final Sort sort;
    private Set<OrganizationId> ids;
    private Set<String> names;

    public OrganizationQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, ID, NAME);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> ids = Arrays.stream(e.split("\\.")).map(OrganizationId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(NAME)).ifPresent(e -> names = Arrays.stream(e.split("\\.")).collect(Collectors.toSet()));
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(new QueryConfig(config));
        this.sort = Sort.byId(true);
    }

    public OrganizationQuery(OrganizationId projectId) {
        ids = new HashSet<>();
        ids.add(projectId);
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
