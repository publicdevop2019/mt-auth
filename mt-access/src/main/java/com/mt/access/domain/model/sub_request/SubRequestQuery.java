package com.mt.access.domain.model.sub_request;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Collections;
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
    private Set<SubRequestStatus> subRequestStatuses;
    private Set<ProjectId> epProjectIds;

    public SubRequestQuery(SubRequestId id) {
        ids = new HashSet<>();
        ids.add(id);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = Sort.byId(true);
    }

    private SubRequestQuery(UserId creatorUserId, String pageParam) {
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(QueryConfig.skipCount());
        this.sort = Sort.byId(true);
        this.createdBy = creatorUserId;
    }

    private SubRequestQuery(String pageParam) {
        setPageConfig(PageConfig.limited(pageParam, 50));
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
                Set<SubRequestStatus> enums = new HashSet<>();
                enums.add(SubRequestStatus.PENDING);
                enums.add(SubRequestStatus.REJECTED);
                enums.add(SubRequestStatus.CANCELLED);
                this.subRequestStatuses = enums;
            } else if (subRequestQueryType.equals(SubRequestQueryType.PENDING_APPROVAL)) {
                this.epProjectIds = DomainRegistry.getCurrentUserService().getTenantIds();
                this.subRequestStatuses = Collections.singleton(SubRequestStatus.PENDING);
            }
        }
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(QueryConfig.countRequired());
        this.sort = Sort.byId(true);
    }

    public static SubRequestQuery mySubscriptions(String pageParam) {
        return new SubRequestQuery(DomainRegistry.getCurrentUserService().getUserId(), pageParam);
    }
    public static SubRequestQuery internalSubscriptions(String pageParam) {
        return new SubRequestQuery(pageParam);
    }


    public enum SubRequestQueryType {
        MY_REQUEST,
        PENDING_APPROVAL,
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
