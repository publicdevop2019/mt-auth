package com.mt.access.port.adapter.persistence.client;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.client.ClientRepository;
import com.mt.access.domain.model.client.Client_;
import com.mt.access.domain.model.client.TokenDetail_;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.domain_event.DomainId_;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.sql.clause.OrderClause;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaClientRepository
    extends JpaRepository<Client, Long>, ClientRepository {
    default Set<ProjectId> getProjectIds() {
        return getProjectIds_();
    }

    default Set<ClientId> allClientIds() {
        return getAllClientIds_();
    }

    @Query("select distinct c.projectId from Client c")
    Set<ProjectId> getProjectIds_();

    @Query("select distinct c.clientId from Client c")
    Set<ClientId> getAllClientIds_();

    @Query("select count(*) from Client")
    Long countTotal_();

    @Query("select count(*) from Client c where c.projectId = ?1")
    Long countProjectTotal_(ProjectId projectId);

    default Client query(ClientId clientId) {
        return QueryBuilderRegistry.getClientSelectQueryBuilder().execute(new ClientQuery(clientId))
            .findFirst().orElse(null);
    }

    default void add(Client client) {
        save(client);
    }

    default void remove(Client client) {
        delete(client);
    }

    default void remove(Collection<Client> clients) {
        deleteAll(clients);
    }

    default SumPagedRep<Client> query(ClientQuery clientQuery) {
        return QueryBuilderRegistry.getClientSelectQueryBuilder().execute(clientQuery);
    }

    default long countTotal() {
        return countTotal_();
    }

    default long countProjectTotal(ProjectId projectId) {
        return countProjectTotal_(projectId);
    }

    @Component
    class JpaCriteriaApiClientAdaptor {
        public SumPagedRep<Client> execute(ClientQuery clientQuery) {
            if (clientQuery.getGrantTypes() != null) {
                return grantTypeSearch(clientQuery);
            }
            if (clientQuery.getResources() != null) {
                return resourceSearch(clientQuery);
            }
            QueryUtility.QueryContext<Client> queryContext =
                QueryUtility.prepareContext(Client.class, clientQuery);
            Optional.ofNullable(clientQuery.getClientIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Client_.CLIENT_ID, queryContext));
            Optional.ofNullable(clientQuery.getResourceFlag()).ifPresent(
                e -> QueryUtility.addBooleanEqualPredicate(e, Client_.ACCESSIBLE, queryContext));
            Optional.ofNullable(clientQuery.getName())
                .ifPresent(e -> QueryUtility.addStringLikePredicate(e, Client_.NAME, queryContext));
            Optional.ofNullable(clientQuery.getProjectIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Client_.PROJECT_ID, queryContext));

            Optional.ofNullable(clientQuery.getResources()).ifPresent(e -> {
                queryContext.getPredicates().add(ResourceIdsPredicateConverter
                    .getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                Optional.ofNullable(queryContext.getCountPredicates())
                    .ifPresent(ee -> ee.add(ResourceIdsPredicateConverter
                        .getPredicate(e, queryContext.getCriteriaBuilder(),
                            queryContext.getCountRoot())));
            });
            Optional.ofNullable(clientQuery.getAccessTokenSecSearch()).ifPresent(e -> {
                queryContext.getPredicates().add(GrantAccessTokenClausePredicateConverter
                    .getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                Optional.ofNullable(queryContext.getCountPredicates())
                    .ifPresent(ee -> ee.add(GrantAccessTokenClausePredicateConverter
                        .getPredicate(e, queryContext.getCriteriaBuilder(),
                            queryContext.getCountRoot())));
            });

            ClientOrderConverter clientSortConverter = new ClientOrderConverter();
            List<Order> orderClause = clientSortConverter
                .getOrderClause(clientQuery.getPageConfig().getRawValue(),
                    queryContext.getCriteriaBuilder(), queryContext.getRoot(),
                    queryContext.getQuery());
            queryContext.setOrder(orderClause);
            return QueryUtility.nativePagedQuery(clientQuery, queryContext);
        }

        private SumPagedRep<Client> grantTypeSearch(ClientQuery query) {
            Set<String> grantTypes = query.getGrantTypes().stream().map(Enum::name).collect(
                Collectors.toSet());
            Set<String> projectIds;
            if (query.getProjectIds() != null) {
                projectIds =
                    query.getProjectIds().stream().map(DomainId::getDomainId).collect(
                        Collectors.toSet());
            } else {
                projectIds = Collections.emptySet();
            }
            return commonAndMappingSearch("client_grant_type_map", "grant_type", grantTypes,
                query.getPageConfig(), projectIds);
        }

        private SumPagedRep<Client> resourceSearch(ClientQuery query) {
            Set<String> resourceIds =
                query.getResources().stream().map(DomainId::getDomainId).collect(
                    Collectors.toSet());
            Set<String> projectIds;
            if (query.getProjectIds() != null) {
                projectIds =
                    query.getProjectIds().stream().map(DomainId::getDomainId).collect(
                        Collectors.toSet());
            } else {
                projectIds = Collections.emptySet();
            }
            return commonOrMappingSearch("resources_map", "domain_id", resourceIds,
                query.getPageConfig(), projectIds);
        }

        private SumPagedRep<Client> commonAndMappingSearch(String table, String field,
                                                           Set<String> mapping,
                                                           PageConfig pageConfig,
                                                           Set<String> projectIds) {
            EntityManager entityManager = QueryUtility.getEntityManager();
            javax.persistence.Query countQuery = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM ( " +
                    "SELECT c.id FROM " + table +
                    " mt LEFT JOIN client c ON mt.id = c.id WHERE mt." + field +
                    " in :mapping" +
                    (projectIds.isEmpty() ? "" : " AND c.project_id IN :projectIds") +
                    " GROUP BY mt.id HAVING COUNT(*) >= :mappingCount" +
                    " ) temp;");
            if (!projectIds.isEmpty()) {
                countQuery.setParameter("projectIds", projectIds);
            }
            countQuery.setParameter("mapping", mapping);
            countQuery.setParameter("mappingCount", mapping.size());
            javax.persistence.Query findQuery = entityManager.createNativeQuery(
                "SELECT c.* FROM " + table +
                    " mt LEFT JOIN client c ON mt.id = c.id WHERE mt." + field +
                    " in :mapping" +
                    (projectIds.isEmpty() ? "" : " AND c.project_id IN :projectIds") +
                    " GROUP BY mt.id HAVING COUNT(*) >= :mappingCount LIMIT :limit OFFSET :offset",
                Client.class);
            if (!projectIds.isEmpty()) {
                findQuery.setParameter("projectIds", projectIds);
            }
            findQuery.setParameter("mapping", mapping);
            findQuery.setParameter("mappingCount", mapping.size());
            findQuery.setParameter("limit", pageConfig.getPageSize());
            findQuery.setParameter("offset", pageConfig.getOffset());
            long count = ((Number) countQuery.getSingleResult()).longValue();
            List<Client> resultList = findQuery.getResultList();
            return new SumPagedRep<>(resultList, count);
        }

        private SumPagedRep<Client> commonOrMappingSearch(String table, String field,
                                                          Set<String> mapping,
                                                          PageConfig pageConfig,
                                                          Set<String> projectIds) {
            EntityManager entityManager = QueryUtility.getEntityManager();
            javax.persistence.Query countQuery = entityManager.createNativeQuery(
                "SELECT COUNT(DISTINCT mt.id) FROM " + table +
                    " mt LEFT JOIN client c ON mt.id = c.id WHERE mt." + field +
                    " in :mapping" +
                    (projectIds.isEmpty() ? "" : " AND c.project_id IN :projectIds"));
            if (!projectIds.isEmpty()) {
                countQuery.setParameter("projectIds", projectIds);
            }
            countQuery.setParameter("mapping", mapping);
            javax.persistence.Query findQuery = entityManager.createNativeQuery(
                "SELECT c.* FROM " + table +
                    " mt LEFT JOIN client c ON mt.id = c.id WHERE mt." + field +
                    " in :mapping" +
                    (projectIds.isEmpty() ? "" : " AND c.project_id IN :projectIds") +
                    " GROUP BY mt.id LIMIT :limit OFFSET :offset",
                Client.class);
            if (!projectIds.isEmpty()) {
                findQuery.setParameter("projectIds", projectIds);
            }
            findQuery.setParameter("mapping", mapping);
            findQuery.setParameter("limit", pageConfig.getPageSize());
            findQuery.setParameter("offset", pageConfig.getOffset());
            long count = ((Number) countQuery.getSingleResult()).longValue();
            List<Client> resultList = findQuery.getResultList();
            return new SumPagedRep<>(resultList, count);
        }

        public static class GrantAccessTokenClausePredicateConverter {

            public static Predicate getPredicate(String query, CriteriaBuilder cb,
                                                 Root<Client> root) {
                String[] split = query.split("\\$");
                List<Predicate> results = new ArrayList<>();
                for (String str : split) {
                    if (str.contains("<=")) {
                        int i = Integer.parseInt(str.replace("<=", ""));
                        results.add(cb.lessThanOrEqualTo(root.get(Client_.TOKEN_DETAIL)
                            .get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS), i));
                    } else if (str.contains(">=")) {
                        int i = Integer.parseInt(str.replace(">=", ""));
                        results.add(cb.greaterThanOrEqualTo(root.get(Client_.TOKEN_DETAIL)
                            .get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS), i));
                    } else if (str.contains("<")) {
                        int i = Integer.parseInt(str.replace("<", ""));
                        results.add(cb.lessThan(root.get(Client_.TOKEN_DETAIL)
                            .get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS), i));
                    } else if (str.contains(">")) {
                        int i = Integer.parseInt(str.replace(">", ""));
                        results.add(cb.greaterThan(root.get(Client_.TOKEN_DETAIL)
                            .get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS), i));
                    } else {
                        throw new DefinedRuntimeException("unsupported query value", "0072",
                            HttpResponseCode.BAD_REQUEST,
                            ExceptionCatalog.ILLEGAL_ARGUMENT);
                    }
                }
                return cb.and(results.toArray(new Predicate[0]));
            }
        }

        private static class ResourceIdsPredicateConverter {
            public static Predicate getPredicate(Set<ClientId> query, CriteriaBuilder cb,
                                                 Root<Client> root) {
                Join<Object, Object> tags = root.join(Client_.RESOURCES);
                CriteriaBuilder.In<Object> clause = cb.in(tags.get(DomainId_.DOMAIN_ID));
                query.forEach(e -> clause.value(e.getDomainId()));
                return clause;
            }
        }

        static class ClientOrderConverter extends OrderClause<Client> {
            @Override
            public List<Order> getOrderClause(String page, CriteriaBuilder cb, Root<Client> root,
                                              AbstractQuery<?> abstractQuery) {
                if (page == null) {
                    Order asc = cb.asc(root.get(Client_.NAME));
                    return Collections.singletonList(asc);
                }
                String[] params = page.split(",");
                HashMap<String, String> orderMap = new HashMap<>();

                for (String param : params) {
                    String[] values = param.split(":");
                    if (values.length > 1) {
                        if (values[0].equals("by") && values[1] != null) {
                            orderMap.put("by", values[1]);
                        }
                        if (values[0].equals("order") && values[1] != null) {
                            orderMap.put("order", values[1]);
                        }
                    }
                }
                if ("name".equalsIgnoreCase(orderMap.get("by"))) {
                    if ("asc".equalsIgnoreCase(orderMap.get("order"))) {
                        Order asc = cb.asc(root.get(Client_.NAME));
                        return Collections.singletonList(asc);
                    } else {
                        Order desc = cb.desc(root.get(Client_.NAME));
                        return Collections.singletonList(desc);
                    }
                } else if ("resourceIndicator".equalsIgnoreCase(orderMap.get("by"))) {
                    if ("asc".equalsIgnoreCase(orderMap.get("order"))) {
                        Order asc = cb.asc(root.get(Client_.ACCESSIBLE));
                        return Collections.singletonList(asc);
                    } else {
                        Order desc = cb.desc(root.get(Client_.ACCESSIBLE));
                        return Collections.singletonList(desc);
                    }
                } else if ("id".equalsIgnoreCase(orderMap.get("by"))) {
                    if ("asc".equalsIgnoreCase(orderMap.get("order"))) {
                        Order asc = cb.asc(root.get(Client_.CLIENT_ID).get(DomainId_.DOMAIN_ID));
                        return Collections.singletonList(asc);
                    } else {
                        Order desc = cb.desc(root.get(Client_.CLIENT_ID).get(DomainId_.DOMAIN_ID));
                        return Collections.singletonList(desc);
                    }
                } else if ("accessTokenValiditySeconds".equalsIgnoreCase(orderMap.get("by"))) {
                    if ("asc".equalsIgnoreCase(orderMap.get("order"))) {
                        Order asc = cb.asc(root.get(Client_.TOKEN_DETAIL)
                            .get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS));
                        return Collections.singletonList(asc);
                    } else {
                        Order desc = cb.desc(root.get(Client_.TOKEN_DETAIL)
                            .get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS));
                        return Collections.singletonList(desc);
                    }
                } else {
                    //default sort
                    if (orderMap.get("by") == null) {
                        Order asc = cb.asc(root.get(Client_.NAME));
                        return Collections.singletonList(asc);
                    } else {
                        throw new DefinedRuntimeException("unsupported order by value", "0073",
                            HttpResponseCode.BAD_REQUEST,
                            ExceptionCatalog.ILLEGAL_ARGUMENT);
                    }
                }
            }
        }
    }

}
