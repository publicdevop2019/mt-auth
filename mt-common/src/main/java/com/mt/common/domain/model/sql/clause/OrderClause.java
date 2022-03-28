package com.mt.common.domain.model.sql.clause;

import java.util.List;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

public abstract class OrderClause<T> {
    protected String entityFieldName;

    public abstract List<Order> getOrderClause(String query, CriteriaBuilder cb, Root<T> root,
                                               AbstractQuery<?> abstractQuery);

}
