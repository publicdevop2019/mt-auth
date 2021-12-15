package com.mt.common.domain.model.sql.clause;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class OrderClause<T> {
    protected String entityFieldName;

    public abstract List<Order> getOrderClause(String query, CriteriaBuilder cb, Root<T> root, AbstractQuery<?> abstractQuery);

}
