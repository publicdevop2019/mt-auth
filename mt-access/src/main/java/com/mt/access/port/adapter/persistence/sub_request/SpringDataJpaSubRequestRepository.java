package com.mt.access.port.adapter.persistence.sub_request;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.sub_request.SubRequest;
import com.mt.access.domain.model.sub_request.SubRequestId;
import com.mt.access.domain.model.sub_request.SubRequestQuery;
import com.mt.access.domain.model.sub_request.SubRequestRepository;
import com.mt.access.domain.model.sub_request.SubRequest_;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;


public interface SpringDataJpaSubRequestRepository extends SubRequestRepository,
    JpaRepository<SubRequest, Long> {
    default SubRequest query(SubRequestId id) {
        return query(new SubRequestQuery(id)).findFirst().orElse(null);
    }

    default void add(SubRequest subRequest) {
        save(subRequest);
    }

    default void remove(SubRequest subRequest) {
        delete(subRequest);
    }

    default SumPagedRep<SubRequest> query(SubRequestQuery query) {
        return QueryBuilderRegistry.getSubRequestAdaptor().execute(query);
    }

    default Set<EndpointId> getSubscribeEndpointIds(UserId userId) {
        return getSubscribeEndpointIds_(userId.getDomainId());
    }

    default Set<UserId> getEndpointSubscriber(EndpointId endpointId) {
        return getEndpointSubscriber_(endpointId);
    }

    @Query("select distinct sr.createdBy from SubRequest sr where sr.endpointId = ?1 and sr.subRequestStatus='APPROVED'")
    Set<UserId> getEndpointSubscriber_(EndpointId id);

    @Query("select distinct sr.endpointId from SubRequest sr where sr.createdBy = ?1 and sr.subRequestStatus='APPROVED'")
    Set<EndpointId> getSubscribeEndpointIds_(String userId);

    default SumPagedRep<SubRequest> getMySubscriptions(SubRequestQuery query) {
        EntityManager entityManager = QueryUtility.getEntityManager();
        TypedQuery<SubRequest> getMySubscriptions =
            entityManager.createNamedQuery("getMySubscriptions", SubRequest.class);
//        getMySubscriptions.setHint("org.hibernate.cacheable", true);
        getMySubscriptions.setParameter("createdBy", query.getCreatedBy().getDomainId());
        List<SubRequest> data = getMySubscriptions
            .setFirstResult(BigDecimal.valueOf(query.getPageConfig().getOffset()).intValue())
            .setMaxResults(query.getPageConfig().getPageSize())
            .getResultList();
        TypedQuery<Long> getMySubscriptionsCount =
            entityManager.createNamedQuery("getMySubscriptionsCount", Long.class);
//        getMySubscriptionsCount.setHint("org.hibernate.cacheable", true);
        getMySubscriptionsCount.setParameter("createdBy", query.getCreatedBy().getDomainId());
        Long count = getMySubscriptionsCount.getSingleResult();
        return new SumPagedRep<>(data, count);
    }

    default SumPagedRep<SubRequest> getSubscription(SubRequestQuery query) {
        EntityManager entityManager = QueryUtility.getEntityManager();
        TypedQuery<SubRequest> getAllSubscriptions =
            entityManager.createNamedQuery("getEpSubscriptions", SubRequest.class);
//        getAllSubscriptions.setHint("org.hibernate.cacheable", true);
        getAllSubscriptions.setParameter("endpointIds", query.getEpIds());
        List<SubRequest> data = getAllSubscriptions
            .setFirstResult(BigDecimal.valueOf(query.getPageConfig().getOffset()).intValue())
            .setMaxResults(query.getPageConfig().getPageSize())
            .getResultList();
        TypedQuery<Long> getAllSubscriptionsCount =
            entityManager.createNamedQuery("getEpSubscriptionsCount", Long.class);
//        getAllSubscriptionsCount.setHint("org.hibernate.cacheable", true);
        getAllSubscriptionsCount.setParameter("endpointIds", query.getEpIds());
        Long count = getAllSubscriptionsCount.getSingleResult();
        return new SumPagedRep<>(data, count);
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
                .addStringEqualPredicate(
                    e.getDomainId(),
                    SubRequest_.CREATED_BY, queryContext));
            Optional.ofNullable(query.getEpProjectIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    SubRequest_.ENDPOINT_PROJECT_ID, queryContext));
            Optional.ofNullable(query.getSubRequestStatuses()).ifPresent(e -> QueryUtility
                .addEnumLiteralEqualPredicate(
                    e,
                    SubRequest_.SUB_REQUEST_STATUS, queryContext));
            Order order = null;
            if (query.getSort().isById()) {
                order = QueryUtility
                    .getDomainIdOrder(SubRequest_.SUB_REQUEST_ID, queryContext,
                        query.getSort().isAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(query, queryContext);
        }
    }

}
