package com.mt.common.domain.model.restful.query;

import com.mt.common.CommonConstant;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.sql.clause.NotDeletedClause;
import com.mt.common.domain.model.sql.exception.UnsupportedQueryException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QueryUtility {
    private static EntityManager em;

    public static <T, S extends QueryCriteria> Set<T> getAllByQuery(
        Function<S, SumPagedRep<T>> ofQuery, S query) {
        SumPagedRep<T> sumPagedRep = ofQuery.apply(query);
        if (sumPagedRep.getData().size() == 0) {
            return new HashSet<>();
        }
        //for accuracy
        double l =
            (double) sumPagedRep.getTotalItemCount() / sumPagedRep.getData().size();
        double ceil = Math.ceil(l);
        int i = BigDecimal.valueOf(ceil).intValue();
        Set<T> data = new HashSet<>(sumPagedRep.getData());
        for (int a = 1; a < i; a++) {
            data.addAll(ofQuery.apply(query.pageOf(a)).getData());
        }
        return data;
    }

    public static <T> SumPagedRep<T> pagedQuery(QueryCriteria queryCriteria,
                                                QueryContext<T> context) {
        //add soft delete
        context.getPredicates().add(new NotDeletedClause<T>()
            .getWhereClause(context.getCriteriaBuilder(), context.getRoot()));
        Optional.ofNullable(context.getCountPredicates()).ifPresent(e -> e.add(
            new NotDeletedClause<T>()
                .getWhereClause(context.getCriteriaBuilder(), context.getCountRoot())));
        return nativePagedQuery(queryCriteria, context);
    }

    //without soft delete check
    public static <T> SumPagedRep<T> nativePagedQuery(QueryCriteria queryCriteria,
                                                      QueryContext<T> context) {
        Predicate and =
            context.getCriteriaBuilder().and(context.getPredicates().toArray(new Predicate[0]));
        List<T> select =
            QueryUtility.select(and, context.getOrder(), queryCriteria.getPageConfig(), context);
        Long count = null;
        if (queryCriteria.count()) {
            Predicate countPredicate = context.getCriteriaBuilder()
                .and(context.getCountPredicates().toArray(new Predicate[0]));
            count = QueryUtility.count(countPredicate, context);
        }
        return new SumPagedRep<>(select, count);
    }

    private static <T> Long count(Predicate predicate, QueryContext<T> context) {
        CriteriaQuery<Long> query = context.getCountQuery();
        query.select(context.getCriteriaBuilder().count(context.getCountRoot()));
        query.where(predicate);
        TypedQuery<Long> query1 = em.createQuery(query);
        ((Query) query1).setHint("org.hibernate.cacheable", true);
        return query1.getSingleResult();
    }

    private static <T> List<T> select(Predicate predicate, List<Order> order, PageConfig page,
                                      QueryContext<T> context) {
        CriteriaQuery<T> query = context.getQuery();
        Root<T> root = context.getRoot();
        query.select(root);
        query.where(predicate);
        query.orderBy(order);
        TypedQuery<T> query1 = em.createQuery(query)
            .setFirstResult(BigDecimal.valueOf(page.getOffset()).intValue())
            .setMaxResults(page.getPageSize());
        ((Query) query1).setHint("org.hibernate.cacheable", true);
        return query1.getResultList();
    }

    public static Map<String, String> parseQuery(String rawQuery, String... supportedFields) {
        Map<String, String> stringStringMap = Optional.ofNullable(rawQuery).map(e -> {
            Map<String, String> parsed = new HashMap<>();
            String[] split = rawQuery.split(",");
            for (String str : split) {
                String[] split1 = str.split(":");
                if (split1.length != 2) {
                    log.info("unable to parse query string {}", rawQuery);
                    throw new QueryParseException();
                }
                parsed.put(split1[0], split1[1]);
            }
            return parsed;
        }).orElseGet(Collections::emptyMap);
        validateQuery(stringStringMap, supportedFields);
        return stringStringMap;
    }

    private static void validateQuery(Map<String, String> parsedMap, String... supportedFields) {
        List<String> list = List.of(supportedFields);
        if (parsedMap.keySet().stream().anyMatch(e -> !list.contains(e))) {
            throw new UnknownQueryValueException();
        }
    }

    public static <T> QueryContext<T> prepareContext(Class<T> clazz, QueryCriteria queryCriteria) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        Root<T> countRoot = null;
        CriteriaQuery<Long> countQuery = null;
        List<Predicate> countPredicate = null;
        if (queryCriteria.count()) {
            countQuery = criteriaBuilder.createQuery(Long.class);
            countRoot = countQuery.from(clazz);
            countPredicate = new ArrayList<>();
        }
        return new QueryContext<>(criteriaBuilder, query, root, countQuery, countRoot, clazz,
            new ArrayList<>(), countPredicate);
    }

    public static <T> void addStringEqualPredicate(String value, String sqlFieldName,
                                                   QueryContext<T> context) {
        context.getPredicates().add(context.getCriteriaBuilder()
            .equal(context.getRoot().get(sqlFieldName).as(String.class), value));
        Optional.ofNullable(context.getCountPredicates()).ifPresent(e -> e.add(
            context.getCriteriaBuilder()
                .equal(context.getCountRoot().get(sqlFieldName).as(String.class), value)));
    }

    public static <T> void addBooleanEqualPredicate(boolean value, String sqlFieldName,
                                                    QueryContext<T> queryContext) {
        if (value) {
            queryContext.getPredicates().add(
                queryContext.getCriteriaBuilder().isTrue(queryContext.getRoot().get(sqlFieldName)));
        } else {
            queryContext.getPredicates().add(queryContext.getCriteriaBuilder()
                .isFalse(queryContext.getRoot().get(sqlFieldName)));
        }
        Optional.ofNullable(queryContext.getCountPredicates()).ifPresent(e -> {
            if (value) {
                e.add(queryContext.getCriteriaBuilder()
                    .isTrue(queryContext.getCountRoot().get(sqlFieldName)));
            } else {
                e.add(queryContext.getCriteriaBuilder()
                    .isFalse(queryContext.getCountRoot().get(sqlFieldName)));
            }
        });
    }

    public static <T> Order getOrder(String fieldName, QueryContext<T> queryContext,
                                     boolean isAsc) {
        Order order;
        if (isAsc) {
            order = queryContext.getCriteriaBuilder().asc(queryContext.getRoot().get(fieldName));
        } else {
            order = queryContext.getCriteriaBuilder().desc(queryContext.getRoot().get(fieldName));
        }
        return order;
    }

    public static <T> Order getDomainIdOrder(String domainIdName, QueryContext<T> queryContext,
                                             boolean isAsc) {
        Order order;
        if (isAsc) {
            order = queryContext.getCriteriaBuilder()
                .asc(queryContext.getRoot().get(domainIdName).get(CommonConstant.DOMAIN_ID));
        } else {
            order = queryContext.getCriteriaBuilder()
                .desc(queryContext.getRoot().get(domainIdName).get(CommonConstant.DOMAIN_ID));
        }
        return order;
    }

    public static <T> void addStringInPredicate(Set<String> collect, String fieldName,
                                                QueryContext<T> queryContext) {
        queryContext.getPredicates()
            .add(queryContext.getRoot().get(fieldName).as(String.class).in(collect));
        Optional.ofNullable(queryContext.getCountPredicates()).ifPresent(
            e -> e.add(queryContext.getCountRoot().get(fieldName).as(String.class).in(collect)));
    }

    public static <T> void addLongInPredicate(Set<Long> collect, String fieldName,
                                              QueryContext<T> queryContext) {
        queryContext.getPredicates()
            .add(queryContext.getRoot().get(fieldName).as(Long.class).in(collect));
        Optional.ofNullable(queryContext.getCountPredicates()).ifPresent(
            e -> e.add(queryContext.getCountRoot().get(fieldName).as(Long.class).in(collect)));
    }

    public static <T> void addDomainIdInPredicate(Set<String> collect, String fieldName,
                                                  QueryContext<T> queryContext) {
        queryContext.getPredicates().add(
            queryContext.getRoot().get(fieldName).get(CommonConstant.DOMAIN_ID).as(String.class)
                .in(collect));
        Optional.ofNullable(queryContext.getCountPredicates()).ifPresent(e -> e.add(
            queryContext.getCountRoot().get(fieldName).get(CommonConstant.DOMAIN_ID)
                .as(String.class).in(collect)));
    }

    public static <T> void addDomainIdIsPredicate(String value, String sqlFieldName,
                                                  QueryContext<T> queryContext) {
        if ("null".equalsIgnoreCase(value)) {
            throw new IllegalArgumentException("should never reached here");
        } else {
            queryContext.getPredicates().add(queryContext.getCriteriaBuilder().equal(
                queryContext.getRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID)
                    .as(String.class), value));
            Optional.ofNullable(queryContext.getCountPredicates()).ifPresent(e -> e.add(
                queryContext.getCriteriaBuilder().equal(
                    queryContext.getCountRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID)
                        .as(String.class), value)));

        }
    }

    public static <T> void addDomainIdIsNullPredicate(String sqlFieldName,
                                                      QueryContext<T> queryContext) {
        queryContext.getPredicates().add(queryContext.getCriteriaBuilder().isNull(
            queryContext.getRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID)
                .as(String.class)));
        Optional.ofNullable(queryContext.getCountPredicates()).ifPresent(e -> e.add(
            queryContext.getCriteriaBuilder().isNull(
                queryContext.getCountRoot().get(sqlFieldName).get(CommonConstant.DOMAIN_ID)
                    .as(String.class))));
    }

    public static <T> void addStringLikePredicate(String value, String sqlFieldName,
                                                  QueryContext<T> queryContext) {
        queryContext.getPredicates().add(queryContext.getCriteriaBuilder()
            .like(queryContext.getRoot().get(sqlFieldName).as(String.class),
                "%" + value.trim() + "%"));
        Optional.ofNullable(queryContext.getCountPredicates()).ifPresent(e -> e.add(
            queryContext.getCriteriaBuilder()
                .like(queryContext.getCountRoot().get(sqlFieldName).as(String.class),
                    "%" + value.trim() + "%")));
    }

    public static <T> void addNumberRagePredicate(String query, String entityFieldName,
                                                  QueryContext<T> queryContext) {
        Predicate numPredicate = QueryUtility
            .getNumPredicate(query, queryContext.getCriteriaBuilder(), queryContext.getRoot(),
                entityFieldName);
        queryContext.getPredicates().add(numPredicate);
        Optional.ofNullable(queryContext.getCountPredicates()).ifPresent(e -> {
            Root<T> countRoot = queryContext.getCountRoot();
            Predicate numPredicate1 = QueryUtility
                .getNumPredicate(query, queryContext.getCriteriaBuilder(), countRoot,
                    entityFieldName);
            e.add(numPredicate1);
        });
    }

    private static <T> Predicate getNumPredicate(String query, CriteriaBuilder cb, Root<T> root,
                                                 String entityFieldName) {
        String[] split = query.split("\\$");
        List<Predicate> results = new ArrayList<>();
        for (String str : split) {
            if (str.contains("<=")) {
                int i = Integer.parseInt(str.replace("<=", ""));
                results.add(cb.lessThanOrEqualTo(root.get(entityFieldName), i));
            } else if (str.contains(">=")) {
                int i = Integer.parseInt(str.replace(">=", ""));
                results.add(cb.greaterThanOrEqualTo(root.get(entityFieldName), i));
            } else if (str.contains("<")) {
                int i = Integer.parseInt(str.replace("<", ""));
                results.add(cb.lessThan(root.get(entityFieldName), i));
            } else if (str.contains(">")) {
                int i = Integer.parseInt(str.replace(">", ""));
                results.add(cb.greaterThan(root.get(entityFieldName), i));
            } else {
                throw new UnsupportedQueryException();
            }
        }
        return cb.and(results.toArray(new Predicate[0]));
    }

    public static <T> void addEnumLiteralEqualPredicate(Set<? extends Enum<?>> e, String type,
                                                        QueryContext<T> queryContext) {
        List<Predicate> list2 = new ArrayList<>();
        for (Enum<?> str : e) {
            list2.add(queryContext.getCriteriaBuilder()
                .equal(queryContext.getRoot().get(type).as(String.class), str.name()));
        }
        Predicate or = queryContext.getCriteriaBuilder().or(list2.toArray(Predicate[]::new));
        queryContext.getPredicates().add(or);
        Optional.ofNullable(queryContext.getCountPredicates()).ifPresent(ee -> {
            List<Predicate> list3 = new ArrayList<>();
            for (Enum<?> str : e) {
                list3.add(queryContext.getCriteriaBuilder()
                    .equal(queryContext.getCountRoot().get(type).as(String.class), str.name()));
            }
            ee.add(queryContext.getCriteriaBuilder().or(list3.toArray(Predicate[]::new)));
        });
    }


    @Autowired
    public void setEntityManager(EntityManager em) {
        QueryUtility.em = em;
    }

    public static class QueryParseException extends RuntimeException {
    }

    public static class UnknownQueryValueException extends RuntimeException {
    }

    @Getter
    public static class QueryContext<T> {
        private final CriteriaBuilder criteriaBuilder;
        private final Root<T> root;
        private final Root<T> countRoot;
        private final CriteriaQuery<Long> countQuery;
        private final CriteriaQuery<T> query;
        private final Class<T> clazz;
        private final List<Predicate> predicates;
        private final List<Predicate> countPredicates;
        private List<Order> order;

        public QueryContext(CriteriaBuilder cb, CriteriaQuery<T> query, Root<T> root,
                            CriteriaQuery<Long> countQuery, Root<T> countRoot, Class<T> clazz,
                            List<Predicate> predicates, List<Predicate> countPredicates) {
            this.criteriaBuilder = cb;
            this.root = root;
            this.countQuery = countQuery;
            this.query = query;
            this.clazz = clazz;
            this.predicates = predicates;
            this.countRoot = countRoot;
            this.countPredicates = countPredicates;
        }

        public void setOrder(Order order) {
            this.order = List.of(order);
        }

        public void setOrder(List<Order> order) {
            this.order = order;
        }
    }
}
