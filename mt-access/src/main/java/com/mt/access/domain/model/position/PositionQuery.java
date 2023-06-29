package com.mt.access.domain.model.position;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PositionQuery extends QueryCriteria {
    private static final String ID = "id";
    private static final String NAME = "name";
    private final Sort sort;
    private Set<PositionId> ids;
    private Set<String> names;

    public PositionQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, ID, NAME);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> ids =
            Arrays.stream(e.split("\\.")).map(PositionId::new).collect(Collectors.toSet()));
        Optional.ofNullable(stringStringMap.get(NAME))
            .ifPresent(e -> names = Arrays.stream(e.split("\\.")).collect(Collectors.toSet()));
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(new QueryConfig(config));
        this.sort = Sort.byId(true);
    }

    public PositionQuery(PositionId projectId) {
        ids = new HashSet<>();
        ids.add(projectId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = Sort.byId(true);
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
