package com.mt.common.port.adapter.persistence.idempotent;

import com.mt.common.domain.model.idempotent.ChangeRecord;
import com.mt.common.domain.model.idempotent.ChangeRecordQuery;
import com.mt.common.domain.model.idempotent.ChangeRecordRepository;
import com.mt.common.domain.model.idempotent.ChangeRecord_;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.port.adapter.persistence.CommonQueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;

/**
 * simple idempotent support
 */
@Repository
public interface SpringDataJpaChangeRecordRepository extends ChangeRecordRepository, JpaRepository<ChangeRecord, Long> {

    default SumPagedRep<ChangeRecord> changeRecordsOfQuery(ChangeRecordQuery changeRecordQuery) {
        return CommonQueryBuilderRegistry.getChangeRecordQueryBuilder().execute(changeRecordQuery);
    }

    default void addForwardChangeRecord(ChangeRecord changeRecord) {
        save(changeRecord);
    }

    default void addReverseChangeRecord(ChangeRecord changeRecord) {
        save(changeRecord);
    }

    default void addEmptyReverseChangeRecord(ChangeRecord changeRecord) {
        save(changeRecord);
    }

    default void addEmptyForwardChangeRecord(ChangeRecord changeRecord) {
        save(changeRecord);
    }

    @Component
    class SpringDataJpaCriteriaApiChangeRecordAdaptor {
        public SumPagedRep<ChangeRecord> execute(ChangeRecordQuery query) {
            QueryUtility.QueryContext<ChangeRecord> context = QueryUtility.prepareContext(ChangeRecord.class, query);
            Optional.ofNullable(query.getChangeIds()).ifPresent(e -> QueryUtility.addStringInPredicate(e, ChangeRecord_.CHANGE_ID, context));
            Optional.ofNullable(query.getEntityType()).ifPresent(e -> QueryUtility.addStringEqualPredicate(e, ChangeRecord_.ENTITY_TYPE, context));
            Order order = null;
            if (query.getChangeRecordSort().isById())
                order = QueryUtility.getOrder(ChangeRecord_.CHANGE_ID, context, query.getChangeRecordSort().isAsc());
            context.setOrder(order);
            return QueryUtility.nativePagedQuery(query, context);
        }
    }
}
