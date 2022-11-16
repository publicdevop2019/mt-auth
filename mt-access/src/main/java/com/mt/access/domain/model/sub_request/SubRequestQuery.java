package com.mt.access.domain.model.sub_request;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;

@Getter
public class SubRequestQuery extends QueryCriteria {
    private static final String TYPE = "type";
    private final Sort sort;
    private Set<SubRequestId> ids;
    private UserId createdBy;
    private SubRequestStatus subRequestStatus;
    private Set<ProjectId> epProjectIds;

    public SubRequestQuery(SubRequestId id) {
        ids = new HashSet<>();
        ids.add(id);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = Sort.byId(true);
    }

    public SubRequestQuery(String queryParam, String pageParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, TYPE);
        String s = stringStringMap.get(TYPE);
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException("type is required");
        } else {
            SubRequestQueryType subRequestQueryType;
            try {
                subRequestQueryType = SubRequestQueryType.valueOf(s.toUpperCase());
            } catch (NullPointerException ex) {
                throw new IllegalArgumentException("missing sub request query type");
            }
            if (subRequestQueryType.equals(SubRequestQueryType.MY_REQUEST)) {
                this.createdBy = DomainRegistry.getCurrentUserService().getUserId();
            } else if (subRequestQueryType.equals(SubRequestQueryType.PENDING_APPROVAL)) {
                this.epProjectIds = DomainRegistry.getCurrentUserService().getTenantIds();
                this.subRequestStatus = SubRequestStatus.PENDING;
            } else if (subRequestQueryType.equals(SubRequestQueryType.MY_SUBSCRIPTIONS)) {
                this.createdBy = DomainRegistry.getCurrentUserService().getUserId();
                this.subRequestStatus = SubRequestStatus.APPROVED;
            }

        }
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(QueryConfig.countRequired());
        this.sort = Sort.byId(true);
    }

    public enum SubRequestQueryType {
        MY_REQUEST,
        PENDING_APPROVAL,
        MY_SUBSCRIPTIONS
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
