package com.mt.common.domain.model.sql.clause;

import com.mt.common.domain.model.audit.Auditable_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public class NotDeletedClause<T> {
    public NotDeletedClause() {
    }

    public Predicate getWhereClause(CriteriaBuilder cb, Root<T> root) {
        return cb.or(cb.isNull(root.get(Auditable_.DELETED)), cb.equal(root.get(Auditable_.DELETED), 0L));
    }
}
