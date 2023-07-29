package com.mt.common.port.adapter.persistence.idempotent;

import com.mt.common.domain.model.idempotent.ChangeRecord;
import com.mt.common.domain.model.idempotent.ChangeRecordQuery;
import com.mt.common.domain.model.idempotent.ChangeRecordRepository;
import com.mt.common.domain.model.idempotent.ChangeRecord_;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.port.adapter.persistence.CommonQueryBuilderRegistry;
import java.util.Optional;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * simple idempotent support.
 */
@Repository
public interface SpringDataJpaChangeRecordRepository
    extends ChangeRecordRepository, JpaRepository<ChangeRecord, Long> {

    default SumPagedRep<ChangeRecord> query(ChangeRecordQuery changeRecordQuery) {
        return CommonQueryBuilderRegistry.getChangeRecordQueryBuilder().execute(changeRecordQuery);
    }

    default Optional<ChangeRecord> internalQuery(String changeId, String entityType) {
        return _findByChangeIdAndEntityType(changeId, entityType);
    }

    @Query("select c from #{#entityName} c where c.changeId = ?1 and c.entityType = ?2")
    Optional<ChangeRecord> _findByChangeIdAndEntityType(String changeId, String entityType);

    default void add(ChangeRecord changeRecord) {
        save(changeRecord);
    }

    @Component
    class SpringDataJpaCriteriaApiChangeRecordAdaptor {
        public SumPagedRep<ChangeRecord> execute(ChangeRecordQuery query) {
            QueryUtility.QueryContext<ChangeRecord> context =
                QueryUtility.prepareContext(ChangeRecord.class, query);
            Optional.ofNullable(query.getChangeIds()).ifPresent(
                e -> QueryUtility.addStringInPredicate(e, ChangeRecord_.CHANGE_ID, context));
            Optional.ofNullable(query.getEntityType()).ifPresent(
                e -> QueryUtility.addStringEqualPredicate(e, ChangeRecord_.ENTITY_TYPE, context));
            Order order = null;
            if (Checker.isTrue(query.getSort().getById())) {
                order = QueryUtility
                    .getOrder(ChangeRecord_.CHANGE_ID, context, query.getSort().getIsAsc());
            }
            context.setOrder(order);
            return QueryUtility.nativePagedQuery(query, context);
        }
    }
}
