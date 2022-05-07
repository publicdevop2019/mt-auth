package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StoredEventQuery extends QueryCriteria {
    public static final String ID = "id";
    public static final String DOMAIN_ID = "domainId";
    private Set<Long> ids;
    private Boolean send;
    private Set<String> domainIds;
    private Set<String> names;
    private DomainEventSort sort;

    public StoredEventQuery(String queryParam, String pageParam, String skipCount) {
        setQueryConfig(new QueryConfig(skipCount));
        setPageConfig(PageConfig.limited(pageParam, 200));
        updateQueryParam(queryParam);
        setSort(pageConfig);
    }

    public StoredEventQuery(Set<String> name, String queryParam, String pageParam,
                            String skipCount) {
        setQueryConfig(new QueryConfig(skipCount));
        setPageConfig(PageConfig.limited(pageParam, 200));
        this.names = name;
        updateQueryParam(queryParam);
        setSort(pageConfig);
    }

    public static StoredEventQuery notSend() {
        StoredEventQuery storedEventQuery = new StoredEventQuery();
        storedEventQuery.sort = DomainEventSort.byId(true);
        storedEventQuery.send = false;
        storedEventQuery.setPageConfig(PageConfig.defaultConfig());
        storedEventQuery.setQueryConfig(QueryConfig.skipCount());
        return storedEventQuery;
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, ID, DOMAIN_ID);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            this.ids =
                Arrays.stream(e.split("\\.")).map(Long::parseLong).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(DOMAIN_ID)).ifPresent(e -> {
            this.domainIds = Arrays.stream(e.split("\\.")).collect(Collectors.toSet());
        });
    }

    private void setSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase("id")) {
            this.sort = DomainEventSort.byId(pageConfig.isSortOrderAsc());
        }
    }

    @Getter
    public static class DomainEventSort {
        private final boolean isAsc;
        private boolean isById;

        private DomainEventSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static DomainEventSort byId(boolean isAsc) {
            DomainEventSort skuSort = new DomainEventSort(isAsc);
            skuSort.isById = true;
            return skuSort;
        }
    }
}
