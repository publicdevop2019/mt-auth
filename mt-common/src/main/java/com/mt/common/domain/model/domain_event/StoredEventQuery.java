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
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class StoredEventQuery extends QueryCriteria {
    public static final String ID = "id";
    public static final String DOMAIN_ID = "domainId";
    public static final String ROUTABLE = "routable";
    public static final String REJECTED = "rejected";
    private Set<Long> ids;
    private Boolean send;
    private Boolean routable;
    private Set<String> domainIds;
    private Set<String> names;
    private DomainEventSort sort;
    private Boolean rejected;

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
        Map<String, String> stringStringMap =
            QueryUtility.parseQuery(queryParam, ID, DOMAIN_ID, ROUTABLE, REJECTED);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            this.ids =
                Arrays.stream(e.split("\\.")).map(Long::parseLong).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get(ROUTABLE)).ifPresent(e -> {
            this.routable = e.equals("1");
        });
        Optional.ofNullable(stringStringMap.get(REJECTED)).ifPresent(e -> {
            this.rejected = e.equals("1");
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
        private final Boolean isAsc;
        private Boolean byId;

        private DomainEventSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static DomainEventSort byId(boolean isAsc) {
            DomainEventSort skuSort = new DomainEventSort(isAsc);
            skuSort.byId = true;
            return skuSort;
        }
    }
}
