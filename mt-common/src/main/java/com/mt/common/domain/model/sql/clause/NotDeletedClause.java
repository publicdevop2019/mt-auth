package com.mt.common.domain.model.sql.clause;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.mt.common.domain.model.audit.Auditable.ENTITY_DELETED;

public class NotDeletedClause<T> {
    public NotDeletedClause() {
    }

    public Predicate getWhereClause(CriteriaBuilder cb, Root<T> root) {
        return cb.or(cb.isNull(root.get(ENTITY_DELETED)), cb.isFalse(root.get(ENTITY_DELETED)));
    }
}
