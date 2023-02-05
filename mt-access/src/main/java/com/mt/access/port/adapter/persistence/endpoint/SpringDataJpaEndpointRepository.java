package com.mt.access.port.adapter.persistence.endpoint;

import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.endpoint.EndpointRepository;
import com.mt.access.domain.model.endpoint.Endpoint_;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaEndpointRepository
    extends JpaRepository<Endpoint, Long>, EndpointRepository {

    default Optional<Endpoint> endpointOfId(EndpointId endpointId) {
        return endpointsOfQuery(new EndpointQuery(endpointId)).findFirst();
    }

    default Set<CacheProfileId> getCacheProfileIds() {
        return getCacheProfileIds_();
    }

    default Set<CorsProfileId> getCorsProfileIds() {
        return getCorsProfileIds_();
    }

    default Set<ClientId> getClientIds() {
        return getClientIds_();
    }

    @Query("select distinct ep.cacheProfileId from Endpoint ep where ep.cacheProfileId is not null")
    Set<CacheProfileId> getCacheProfileIds_();

    @Query("select distinct ep.corsProfileId from Endpoint ep where ep.corsProfileId is not null")
    Set<CorsProfileId> getCorsProfileIds_();

    @Query("select distinct ep.clientId from Endpoint ep")
    Set<ClientId> getClientIds_();

    default void add(Endpoint endpoint) {
        save(endpoint);
    }

    default void remove(Endpoint endpoint) {
        endpoint.softDelete();
        save(endpoint);
    }

    default void remove(Collection<Endpoint> endpoints) {
        endpoints.forEach(Auditable::softDelete);
        saveAll(endpoints);
    }

    default SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery query) {
        return QueryBuilderRegistry.getEndpointQueryBuilder().execute(query);
    }


    @Component
    class JpaCriteriaApiEndpointAdapter {
        public SumPagedRep<Endpoint> execute(EndpointQuery endpointQuery) {
            QueryUtility.QueryContext<Endpoint> queryContext =
                QueryUtility.prepareContext(Endpoint.class, endpointQuery);
            Optional.ofNullable(endpointQuery.getEndpointIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Endpoint_.ENDPOINT_ID, queryContext));
            Optional.ofNullable(endpointQuery.getClientIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Endpoint_.CLIENT_ID, queryContext));
            Optional.ofNullable(endpointQuery.getProjectIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Endpoint_.PROJECT_ID, queryContext));
            Optional.ofNullable(endpointQuery.getPermissionIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Endpoint_.PERMISSION_ID, queryContext));
            Optional.ofNullable(endpointQuery.getCacheProfileIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Endpoint_.CACHE_PROFILE_ID, queryContext));
            Optional.ofNullable(endpointQuery.getPath()).ifPresent(
                e -> QueryUtility.addStringEqualPredicate(e, Endpoint_.PATH, queryContext));
            Optional.ofNullable(endpointQuery.getMethod()).ifPresent(
                e -> QueryUtility.addStringEqualPredicate(e, Endpoint_.METHOD, queryContext));
            Optional.ofNullable(endpointQuery.getIsWebsocket()).ifPresent(e -> QueryUtility
                .addBooleanEqualPredicate(e, Endpoint_.IS_WEBSOCKET, queryContext));
            Optional.ofNullable(endpointQuery.getIsShared()).ifPresent(
                e -> {
                    if (e) {
                        queryContext.getPredicates().add(
                            MarketAvailableEndpointPredicateConverter
                                .getPredicate(queryContext.getCriteriaBuilder(),
                                    queryContext.getRoot()));
                        Optional.ofNullable(queryContext.getCountPredicates())
                            .ifPresent(ee -> ee.add(
                                MarketAvailableEndpointPredicateConverter
                                    .getPredicate(queryContext.getCriteriaBuilder(),
                                        queryContext.getCountRoot())));
                    }
                }
            );

            Optional.ofNullable(endpointQuery.getIsSecured()).ifPresent(
                e -> QueryUtility.addBooleanEqualPredicate(e, Endpoint_.AUTH_REQUIRED, queryContext));
            Optional.ofNullable(endpointQuery.getCorsProfileIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Endpoint_.CORS_PROFILE_ID, queryContext));
            Order order = null;
            if (endpointQuery.getEndpointSort().isById()) {
                order = QueryUtility.getDomainIdOrder(Endpoint_.ENDPOINT_ID, queryContext,
                    endpointQuery.getEndpointSort().isAsc());
            }
            if (endpointQuery.getEndpointSort().isByClientId()) {
                order = QueryUtility.getOrder(Endpoint_.CLIENT_ID, queryContext,
                    endpointQuery.getEndpointSort().isAsc());
            }
            if (endpointQuery.getEndpointSort().isByPath()) {
                order = QueryUtility.getOrder(Endpoint_.PATH, queryContext,
                    endpointQuery.getEndpointSort().isAsc());
            }
            if (endpointQuery.getEndpointSort().isByMethod()) {
                order = QueryUtility.getOrder(Endpoint_.METHOD, queryContext,
                    endpointQuery.getEndpointSort().isAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(endpointQuery, queryContext);
        }

        private static class MarketAvailableEndpointPredicateConverter {
            public static Predicate getPredicate(CriteriaBuilder cb,
                                                 Root<Endpoint> root) {
                Predicate aTrue = cb.isTrue(root.get(Endpoint_.SHARED));
                Predicate aFalse = cb.isFalse(root.get(Endpoint_.AUTH_REQUIRED));
                return cb.or(aTrue, aFalse);
            }
        }
    }
}
