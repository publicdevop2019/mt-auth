package com.mt.access.domain.model.sub_request;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SubRequestQuery extends QueryCriteria {
    private static final String TYPE = "type";
    private Set<SubRequestId> ids;
    private Set<EndpointId> epIds;
    private UserId createdBy;
    private Set<SubRequestStatus> subRequestStatuses;
    private Set<ProjectId> epProjectIds;

    public SubRequestQuery(SubRequestId id) {
        ids = new HashSet<>();
        ids.add(id);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    private SubRequestQuery(UserId creatorUserId, String pageParam) {
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(QueryConfig.skipCount());
        this.createdBy = creatorUserId;
    }

    private SubRequestQuery(String pageParam) {
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(QueryConfig.skipCount());
    }

    public SubRequestQuery(String queryParam, String pageParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam, TYPE);
        String s = stringStringMap.get(TYPE);
        if (s == null || s.isBlank()) {
            throw new DefinedRuntimeException("missing sub request query type", "1060",
                HttpResponseCode.BAD_REQUEST);
        } else {
            SubRequestQueryType subRequestQueryType;
            try {
                subRequestQueryType = SubRequestQueryType.valueOf(s.toUpperCase());
            } catch (NullPointerException ex) {
                throw new DefinedRuntimeException("missing sub request query type", "1061",
                    HttpResponseCode.BAD_REQUEST, ex);
            }
            if (subRequestQueryType.equals(SubRequestQueryType.MY_REQUEST)) {
                this.createdBy = DomainRegistry.getCurrentUserService().getUserId();
                Set<SubRequestStatus> enums = new HashSet<>();
                enums.add(SubRequestStatus.PENDING);
                enums.add(SubRequestStatus.REJECTED);
                this.subRequestStatuses = enums;
            } else if (subRequestQueryType.equals(SubRequestQueryType.PENDING_APPROVAL)) {
                this.epProjectIds = DomainRegistry.getCurrentUserService().getTenantIds();
                this.subRequestStatuses = Collections.singleton(SubRequestStatus.PENDING);
            }
        }
        setPageConfig(PageConfig.limited(pageParam, 50));
        setQueryConfig(QueryConfig.countRequired());
    }

    public SubRequestQuery(Set<EndpointId> epIds) {
        this.epIds = epIds;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public static SubRequestQuery mySubscriptions(String pageParam) {
        return new SubRequestQuery(DomainRegistry.getCurrentUserService().getUserId(), pageParam);
    }

    public enum SubRequestQueryType {
        MY_REQUEST,
        PENDING_APPROVAL,
    }
}
