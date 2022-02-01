package com.mt.access.port.adapter.persistence.endpoint;

import com.mt.access.domain.model.endpoint.*;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface SpringDataJpaEndpointRepository extends JpaRepository<Endpoint, Long>, EndpointRepository {

    default Optional<Endpoint> endpointOfId(EndpointId endpointId) {
        return endpointsOfQuery(new EndpointQuery(endpointId)).findFirst();
    }


    default void add(Endpoint endpoint) {
        save(endpoint);
    }

    default void remove(Endpoint endpoint) {
        endpoint.setDeleted(true);
        save(endpoint);
    }

    default void remove(Collection<Endpoint> endpoints) {
        endpoints.forEach(e -> {
            e.setDeleted(true);
        });
        saveAll(endpoints);
    }

    default SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery query) {
        return QueryBuilderRegistry.getEndpointQueryBuilder().execute(query);
    }


    @Component
    class JpaCriteriaApiEndpointAdapter {
        public SumPagedRep<Endpoint> execute(EndpointQuery endpointQuery) {
            QueryUtility.QueryContext<Endpoint> queryContext = QueryUtility.prepareContext(Endpoint.class, endpointQuery);
            Optional.ofNullable(endpointQuery.getEndpointIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Endpoint_.ENDPOINT_ID, queryContext));
            Optional.ofNullable(endpointQuery.getClientIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Endpoint_.CLIENT_ID, queryContext));
            Optional.ofNullable(endpointQuery.getProjectIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Endpoint_.PROJECT_ID, queryContext));
            Optional.ofNullable(endpointQuery.getSystemRoleIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Endpoint_.SYSTEM_ROLE_ID, queryContext));
            Optional.ofNullable(endpointQuery.getCacheProfileIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Endpoint_.CACHE_PROFILE_ID, queryContext));
            Optional.ofNullable(endpointQuery.getPath()).ifPresent(e -> QueryUtility.addStringEqualPredicate(e, Endpoint_.PATH, queryContext));
            Optional.ofNullable(endpointQuery.getMethod()).ifPresent(e -> QueryUtility.addStringEqualPredicate(e, Endpoint_.METHOD, queryContext));
            Optional.ofNullable(endpointQuery.getCorsProfileIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Endpoint_.CORS_PROFILE_ID, queryContext));
            Order order = null;
            if (endpointQuery.getEndpointSort().isById())
                order = QueryUtility.getDomainIdOrder(Endpoint_.ENDPOINT_ID, queryContext, endpointQuery.getEndpointSort().isAsc());
            if (endpointQuery.getEndpointSort().isByClientId())
                order = QueryUtility.getOrder(Endpoint_.CLIENT_ID, queryContext, endpointQuery.getEndpointSort().isAsc());
            if (endpointQuery.getEndpointSort().isByPath())
                order = QueryUtility.getOrder(Endpoint_.PATH, queryContext, endpointQuery.getEndpointSort().isAsc());
            if (endpointQuery.getEndpointSort().isByMethod())
                order = QueryUtility.getOrder(Endpoint_.METHOD, queryContext, endpointQuery.getEndpointSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(endpointQuery, queryContext);
        }


    }
}
