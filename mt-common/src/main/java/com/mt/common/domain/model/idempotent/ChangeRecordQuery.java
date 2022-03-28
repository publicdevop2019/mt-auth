package com.mt.common.domain.model.idempotent;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeRecordQuery extends QueryCriteria {
    public static final String ENTITY_TYPE = "entityType";
    public static final String CHANGE_ID = "changeId";
    private String entityType;
    private Set<String> changeIds;
    private Sort sort;

    public ChangeRecordQuery(String queryParam) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        sort = Sort.instance;
    }

    public ChangeRecordQuery(String queryParam, String entityType) {
        updateQueryParam(queryParam);
        this.entityType = entityType;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        sort = Sort.instance;
    }

    public ChangeRecordQuery(String queryParam, String pageConfig, String queryConfig) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageConfig, 100));
        setQueryConfig(new QueryConfig(queryConfig));
        sort = Sort.instance;
    }

    public static ChangeRecordQuery idempotentQuery(String changeId, String aggregateName) {
        ChangeRecordQuery changeRecordQuery = new ChangeRecordQuery();
        changeRecordQuery.sort = Sort.instance;
        changeRecordQuery.changeIds = Collections.singleton(changeId);
        changeRecordQuery.entityType = aggregateName;
        changeRecordQuery.setPageConfig(PageConfig.defaultConfig());
        changeRecordQuery.setQueryConfig(QueryConfig.skipCount());
        return changeRecordQuery;
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap =
            QueryUtility.parseQuery(queryParam, ENTITY_TYPE, CHANGE_ID);
        entityType = stringStringMap.get(ENTITY_TYPE);
        Optional.ofNullable(stringStringMap.get(CHANGE_ID)).ifPresent(e -> {
            changeIds = new HashSet<>(List.of(e.split("\\.")));
        });
    }

    @Getter
    public static class Sort {
        private static final Sort instance = new Sort();
        private final boolean byId = true;
        private final boolean isAsc = true;

        private Sort() {
        }
    }
}
