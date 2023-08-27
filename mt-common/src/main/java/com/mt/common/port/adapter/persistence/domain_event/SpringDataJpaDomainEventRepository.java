package com.mt.common.port.adapter.persistence.domain_event;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventRepository;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.StoredEventQuery;
import com.mt.common.domain.model.domain_event.StoredEvent_;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.port.adapter.persistence.CommonQueryBuilderRegistry;
import java.util.Optional;
import javax.persistence.criteria.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaDomainEventRepository
    extends CrudRepository<StoredEvent, Long>, DomainEventRepository {

    default void append(StoredEvent event) {
        //for migrated logic use native save instead of hibernate
        if (event.getName().equalsIgnoreCase("USER_MFA_NOTIFICATION")) {
            CommonDomainRegistry.getJdbcTemplate()
                .update("INSERT INTO stored_event " +
                        "(" +
                        "id, " +
                        "domain_id, " +
                        "event_body, " +
                        "internal, " +
                        "name, " +
                        "timestamp, " +
                        "topic, " +
                        "send, " +
                        "routable, " +
                        "rejected, " +
                        "application_id, " +
                        "trace_id" +
                        ") VALUES" +
                        "(?,?,?,?,?,?,?,?,?,?,?,?)",
                    event.getId(),
                    event.getDomainId(),
                    event.getEventBody(),
                    event.getInternal(),
                    event.getName(),
                    event.getTimestamp(),
                    event.getTopic(),
                    event.getSend(),
                    event.getRoutable(),
                    event.getRejected(),
                    event.getApplicationId(),
                    event.getTraceId()
                );
        } else {
            save(event);
        }
    }

    default StoredEvent getById(long id) {
        return findById(id).orElse(null);
    }

    @Override
    default SumPagedRep<StoredEvent> query(StoredEventQuery query) {
        return CommonQueryBuilderRegistry.getStoredEventQueryAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiStoredEventQueryAdapter {
        public SumPagedRep<StoredEvent> execute(StoredEventQuery query) {
            QueryUtility.QueryContext<StoredEvent> queryContext =
                QueryUtility.prepareContext(StoredEvent.class, query);
            Optional.ofNullable(query.getIds())
                .ifPresent(e -> QueryUtility.addLongInPredicate(e, StoredEvent_.ID, queryContext));
            Optional.ofNullable(query.getNames())
                .ifPresent(
                    e -> QueryUtility.addStringInPredicate(e, StoredEvent_.NAME, queryContext));
            Optional.ofNullable(query.getDomainIds()).ifPresent(
                e -> QueryUtility.addStringInPredicate(e, StoredEvent_.DOMAIN_ID, queryContext));
            Optional.ofNullable(query.getSend()).ifPresent(
                e -> QueryUtility.addBooleanEqualPredicate(e, StoredEvent_.SEND, queryContext));
            Optional.ofNullable(query.getRoutable()).ifPresent(
                e -> QueryUtility.addBooleanEqualPredicate(e, StoredEvent_.ROUTABLE, queryContext));
            Optional.ofNullable(query.getRejected()).ifPresent(
                e -> QueryUtility.addBooleanEqualPredicate(e, StoredEvent_.REJECTED, queryContext));
            Order order = null;
            if (Checker.isTrue(query.getSort().getById())) {
                order =
                    QueryUtility.getOrder(StoredEvent_.ID, queryContext,
                        query.getSort().getIsAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(query, queryContext);
        }
    }
}
