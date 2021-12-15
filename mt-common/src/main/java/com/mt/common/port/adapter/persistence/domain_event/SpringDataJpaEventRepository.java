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
public interface SpringDataJpaEventRepository extends CrudRepository<StoredEvent, Long>, EventRepository {
    List<StoredEvent> findByIdGreaterThan(long id);

    default List<StoredEvent> allStoredEventsSince(long lastId) {
        return findByIdGreaterThan(lastId);
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
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addLongInPredicate(query.getIds(), StoredEvent_.ID, queryContext));
            Optional.ofNullable(query.getDomainIds()).ifPresent(e -> QueryUtility.addStringInPredicate(query.getDomainIds(), StoredEvent_.DOMAIN_ID, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getOrder(StoredEvent_.ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(query, queryContext);
        }
    }
}
