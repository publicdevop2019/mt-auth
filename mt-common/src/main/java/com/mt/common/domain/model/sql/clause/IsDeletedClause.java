package com.mt.common.domain.model.sql.clause;

import com.mt.common.domain.model.audit.Auditable_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class IsDeletedClause<T> {
    public IsDeletedClause() {
    }

    public Predicate getWhereClause(CriteriaBuilder cb, Root<T> root) {
        return cb.or(cb.greaterThan(root.get(Auditable_.DELETED), 0L));
    }
}
