package com.mt.common.port.adapter.persistence.domain_event;

import com.mt.common.domain.model.domain_event.*;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.port.adapter.persistence.CommonQueryBuilderRegistry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataJpaDomainEventRepository extends CrudRepository<StoredEvent, Long>, DomainEventRepository {
    List<StoredEvent> findTop50ByIdGreaterThanOrderById(long id);

    default List<StoredEvent> top50StoredEventsSince(long lastId) {
        return findTop50ByIdGreaterThanOrderById(lastId);
    }

    default void append(DomainEvent aDomainEvent) {
        save(new StoredEvent(aDomainEvent));
    }

    default Optional<StoredEvent> getById(long id){
        return findById(id);
    };
    @Override
    default SumPagedRep<StoredEvent> query(StoredEventQuery query) {
        return CommonQueryBuilderRegistry.getStoredEventQueryAdapter().execute(query);
    }

    @Component
    class JpaCriteriaApiStoredEventQueryAdapter {
        public SumPagedRep<StoredEvent> execute(StoredEventQuery query) {
            QueryUtility.QueryContext<StoredEvent> queryContext = QueryUtility.prepareContext(StoredEvent.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(e, StoredEvent_.ID, queryContext));
            Optional.ofNullable(query.getDomainIds()).ifPresent(e -> QueryUtility.addStringInPredicate(e, StoredEvent_.DOMAIN_ID, queryContext));
            Optional.ofNullable(query.getSend()).ifPresent(e -> QueryUtility.addBooleanEqualPredicate(e, StoredEvent_.SEND, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(StoredEvent_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(query, queryContext);
        }
    }
}