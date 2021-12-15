package com.mt.common.domain.model.sql.clause;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static com.mt.common.domain.model.audit.Auditable.ENTITY_DELETED;

public class IsDeletedClause<T> {
    public IsDeletedClause() {
    }

    public Predicate getWhereClause(CriteriaBuilder cb, Root<T> root) {
        return cb.or(cb.isNull(root.get(ENTITY_DELETED)), cb.isTrue(root.get(ENTITY_DELETED)));
    }
}
