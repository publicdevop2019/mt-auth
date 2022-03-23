package com.mt.access.port.adapter.persistence.client;

import com.mt.access.domain.model.client.*;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domainId.DomainId_;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.sql.clause.OrderClause;
import com.mt.common.domain.model.sql.exception.UnsupportedQueryException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public interface SpringDataJpaClientRepository extends JpaRepository<Client, Long>, ClientRepository {
    default Set<ProjectId> getProjectIds() {
        return _getProjectIds();
    }
    default Set<ClientId> allClientIds(){
        return _getAllClientIds();
    }

    @Query("select distinct c.projectId from Client c")
    Set<ProjectId> _getProjectIds();
    @Query("select distinct c.clientId from Client c")
    Set<ClientId> _getAllClientIds();

    default Optional<Client> clientOfId(ClientId clientId) {
        return QueryBuilderRegistry.getClientSelectQueryBuilder().execute(new ClientQuery(clientId)).findFirst();
    }

    default void add(Client client) {
        save(client);
    }

    default void remove(Client client) {
        client.softDelete();
        save(client);
    }

    default void remove(Collection<Client> client) {
        client.forEach(Auditable::softDelete);
        saveAll(client);
    }

    default SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery) {
        return QueryBuilderRegistry.getClientSelectQueryBuilder().execute(clientQuery);
    }

    @Component
    class JpaCriteriaApiClientAdaptor {
        public static final String ENTITY_NAME = "name";

        public SumPagedRep<Client> execute(ClientQuery clientQuery) {
            QueryUtility.QueryContext<Client> queryContext = QueryUtility.prepareContext(Client.class, clientQuery);
            Optional.ofNullable(clientQuery.getClientIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Client_.CLIENT_ID, queryContext));
            Optional.ofNullable(clientQuery.getResourceFlag()).ifPresent(e -> QueryUtility.addBooleanEqualPredicate(e, Client_.ACCESSIBLE, queryContext));
            Optional.ofNullable(clientQuery.getName()).ifPresent(e -> QueryUtility.addStringLikePredicate(e, ENTITY_NAME, queryContext));
            Optional.ofNullable(clientQuery.getProjectIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Client_.PROJECT_ID, queryContext));

            Optional.ofNullable(clientQuery.getGrantTypeSearch()).ifPresent(e -> {
                queryContext.getPredicates().add(GrantEnabledPredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                Optional.ofNullable(queryContext.getCountPredicates())
                        .ifPresent(ee -> ee.add(GrantEnabledPredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getCountRoot())));
            });
            Optional.ofNullable(clientQuery.getClientTypeSearch()).ifPresent(e -> {
                queryContext.getPredicates().add(ClientTypePredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                Optional.ofNullable(queryContext.getCountPredicates())
                        .ifPresent(ee -> ee.add(ClientTypePredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getCountRoot())));
            });
            Optional.ofNullable(clientQuery.getResources()).ifPresent(e -> {
                queryContext.getPredicates().add(ResourceIdsPredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                Optional.ofNullable(queryContext.getCountPredicates())
                        .ifPresent(ee -> ee.add(ResourceIdsPredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getCountRoot())));
            });
            Optional.ofNullable(clientQuery.getAccessTokenSecSearch()).ifPresent(e -> {
                queryContext.getPredicates().add(GrantAccessTokenClausePredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                Optional.ofNullable(queryContext.getCountPredicates())
                        .ifPresent(ee -> ee.add(GrantAccessTokenClausePredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getCountRoot())));
            });

            ClientOrderConverter clientSortConverter = new ClientOrderConverter();
            List<Order> orderClause = clientSortConverter.getOrderClause(clientQuery.getPageConfig().getRawValue(), queryContext.getCriteriaBuilder(), queryContext.getRoot(), queryContext.getQuery());
            queryContext.setOrder(orderClause);
            return QueryUtility.pagedQuery(clientQuery, queryContext);
        }

        public static class GrantAccessTokenClausePredicateConverter {

            public static Predicate getPredicate(String query, CriteriaBuilder cb, Root<Client> root) {
                String[] split = query.split("\\$");
                List<Predicate> results = new ArrayList<>();
                for (String str : split) {
                    if (str.contains("<=")) {
                        int i = Integer.parseInt(str.replace("<=", ""));
                        results.add(cb.lessThanOrEqualTo(root.get(Client_.TOKEN_DETAIL).get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS), i));
                    } else if (str.contains(">=")) {
                        int i = Integer.parseInt(str.replace(">=", ""));
                        results.add(cb.greaterThanOrEqualTo(root.get(Client_.TOKEN_DETAIL).get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS), i));
                    } else if (str.contains("<")) {
                        int i = Integer.parseInt(str.replace("<", ""));
                        results.add(cb.lessThan(root.get(Client_.TOKEN_DETAIL).get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS), i));
                    } else if (str.contains(">")) {
                        int i = Integer.parseInt(str.replace(">", ""));
                        results.add(cb.greaterThan(root.get(Client_.TOKEN_DETAIL).get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS), i));
                    } else {
                        throw new UnsupportedQueryException();
                    }
                }
                return cb.and(results.toArray(new Predicate[0]));
            }
        }

        private static class GrantEnabledPredicateConverter {
            public static Predicate getPredicate(String query, CriteriaBuilder cb, Root<Client> root) {
                if (query.contains("$")) {
                    Set<String> strings = new TreeSet<>(Arrays.asList(query.split("\\$")));
                    List<Predicate> list2 = new ArrayList<>();
                    for (String str : strings) {
                        if ("CLIENT_CREDENTIALS".equalsIgnoreCase(str)) {
                            list2.add(cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + GrantType.CLIENT_CREDENTIALS.name() + "%"));
                        } else if ("PASSWORD".equalsIgnoreCase(str)) {
                            list2.add(cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + GrantType.PASSWORD.name() + "%"));
                        } else if ("AUTHORIZATION_CODE".equalsIgnoreCase(str)) {
                            list2.add(cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + GrantType.AUTHORIZATION_CODE.name() + "%"));
                        } else if ("REFRESH_TOKEN".equalsIgnoreCase(str)) {
                            list2.add(cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + GrantType.REFRESH_TOKEN.name() + "%"));
                        }
                    }
                    return cb.and(list2.toArray(Predicate[]::new));
                } else {
                    return getExpression(query, cb, root);
                }
            }

            private static Predicate getExpression(String str, CriteriaBuilder cb, Root<Client> root) {
                if ("CLIENT_CREDENTIALS".equalsIgnoreCase(str)) {
                    return cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + GrantType.CLIENT_CREDENTIALS.name() + "%");
                } else if ("PASSWORD".equalsIgnoreCase(str)) {
                    return cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + GrantType.PASSWORD.name() + "%");
                } else if ("AUTHORIZATION_CODE".equalsIgnoreCase(str)) {
                    return cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + GrantType.AUTHORIZATION_CODE.name() + "%");
                } else if ("REFRESH_TOKEN".equalsIgnoreCase(str)) {
                    return cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + GrantType.REFRESH_TOKEN.name() + "%");
                } else {
                    return null;
                }
            }
        }
        private static class ClientTypePredicateConverter {
            public static Predicate getPredicate(String query, CriteriaBuilder cb, Root<Client> root) {
                if (query.contains("$")) {
                    Set<String> strings = new TreeSet<>(Arrays.asList(query.split("\\$")));
                    List<Predicate> list2 = new ArrayList<>();
                    for (String str : strings) {
                        if ("ROOT_APPLICATION".equalsIgnoreCase(str)) {
                            list2.add(cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + ClientType.ROOT_APPLICATION.name() + "%"));
                        } else if ("FIRST_PARTY".equalsIgnoreCase(str)) {
                            list2.add(cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + ClientType.FIRST_PARTY.name() + "%"));
                        } else if ("THIRD_PARTY".equalsIgnoreCase(str)) {
                            list2.add(cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + ClientType.THIRD_PARTY.name() + "%"));
                        } else if ("FRONTEND_APP".equalsIgnoreCase(str)) {
                            list2.add(cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + ClientType.FRONTEND_APP.name() + "%"));
                        } else if ("BACKEND_APP".equalsIgnoreCase(str)) {
                            list2.add(cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + ClientType.BACKEND_APP.name() + "%"));
                        }
                    }
                    return cb.and(list2.toArray(Predicate[]::new));
                } else {
                    return getExpression(query, cb, root);
                }
            }

            private static Predicate getExpression(String str, CriteriaBuilder cb, Root<Client> root) {
                if ("ROOT_APPLICATION".equalsIgnoreCase(str)) {
                    return cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + ClientType.ROOT_APPLICATION.name() + "%");
                } else if ("FIRST_PARTY".equalsIgnoreCase(str)) {
                    return cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + ClientType.FIRST_PARTY.name() + "%");
                } else if ("THIRD_PARTY".equalsIgnoreCase(str)) {
                    return cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + ClientType.THIRD_PARTY.name() + "%");
                } else if ("FRONTEND_APP".equalsIgnoreCase(str)) {
                    return cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + ClientType.FRONTEND_APP.name() + "%");
                } else if ("BACKEND_APP".equalsIgnoreCase(str)) {
                    return cb.like(root.get(Client_.GRANT_TYPES).as(String.class), "%" + ClientType.BACKEND_APP.name() + "%");
                } else {
                    return null;
                }
            }
        }

        public static class ResourceIdsPredicateConverter {
            public static Predicate getPredicate(Set<ClientId> query, CriteriaBuilder cb, Root<Client> root) {
                Join<Object, Object> tags = root.join(Client_.RESOURCES);
                CriteriaBuilder.In<Object> clause = cb.in(tags.get(DomainId_.DOMAIN_ID));
                query.forEach(e -> clause.value(e.getDomainId()));
                return clause;
            }
        }

        static class ClientOrderConverter extends OrderClause<Client> {
            @Override
            public List<Order> getOrderClause(String page, CriteriaBuilder cb, Root<Client> root, AbstractQuery<?> abstractQuery) {
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
                        Order asc = cb.asc(root.get(Client_.TOKEN_DETAIL).get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS));
                        return Collections.singletonList(asc);
                    } else {
                        Order desc = cb.desc(root.get(Client_.TOKEN_DETAIL).get(TokenDetail_.ACCESS_TOKEN_VALIDITY_SECONDS));
                        return Collections.singletonList(desc);
                    }
                } else {
                    //default sort
                    if (orderMap.get("by") == null) {
                        Order asc = cb.asc(root.get(Client_.NAME));
                        return Collections.singletonList(asc);
                    } else {
                        throw new UnsupportedQueryException();
                    }
                }
            }
        }
    }

}
