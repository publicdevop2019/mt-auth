package com.mt.access.port.adapter.persistence.sub_request;

import com.mt.access.domain.model.sub_request.SubRequest;
import com.mt.access.domain.model.sub_request.SubRequestId;
import com.mt.access.domain.model.sub_request.SubRequestQuery;
import com.mt.access.domain.model.sub_request.SubRequestRepository;
import com.mt.access.domain.model.sub_request.SubRequest_;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

public interface SpringDataJpaSubRequestRepository extends SubRequestRepository,
    JpaRepository<SubRequest, Long> {
    default Optional<SubRequest> getById(SubRequestId id) {
        return getByQuery(new SubRequestQuery(id)).findFirst();
    }

    default void add(SubRequest subRequest) {
        save(subRequest);
    }

    default void remove(SubRequest subRequest) {
        subRequest.softDelete();
        save(subRequest);
    }

    default SumPagedRep<SubRequest> getByQuery(SubRequestQuery query) {
        return QueryBuilderRegistry.getSubRequestAdaptor().execute(query);
    }

    @Component
    class JpaCriteriaApiSubRequestAdaptor {
        public SumPagedRep<SubRequest> execute(SubRequestQuery query) {
            QueryUtility.QueryContext<SubRequest> queryContext =
                QueryUtility.prepareContext(SubRequest.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    SubRequest_.SUB_REQUEST_ID, queryContext));
            Optional.ofNullable(query.getCreatedBy()).ifPresent(e -> QueryUtility
                .addDomainIdIsPredicate(
                    e.getDomainId(),
                    SubRequest_.CREATED_BY, queryContext));
            Optional.ofNullable(query.getEpProjectIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    SubRequest_.ENDPOINT_PROJECT_ID, queryContext));
            Optional.ofNullable(query.getSubRequestStatus()).ifPresent(e -> QueryUtility
                .addEnumLiteralEqualPredicate(
                    Collections.singleton(e),
                    SubRequest_.SUB_REQUEST_STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById()) {
                order = QueryUtility
                    .getDomainIdOrder(SubRequest_.SUB_REQUEST_ID, queryContext,
                        query.getSort().isAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
