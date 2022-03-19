package com.mt.access.port.adapter.persistence.position;

import com.mt.access.domain.model.position.*;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Order;
import java.util.Optional;
import java.util.stream.Collectors;

public interface SpringDataJpaPositionRepository extends PositionRepository, JpaRepository<Position, Long> {
    default Optional<Position> getById(PositionId id) {
        return getByQuery(new PositionQuery(id)).findFirst();
    }

    default void add(Position Position) {
        save(Position);
    }

    default void remove(Position Position) {
        Position.softDelete();
        save(Position);
    }

    default SumPagedRep<Position> getByQuery(PositionQuery PositionQuery) {
        return QueryBuilderRegistry.getPositionAdaptor().execute(PositionQuery);
    }

    @Component
    class JpaCriteriaApiPositionAdaptor {
        public SumPagedRep<Position> execute(PositionQuery query) {
            QueryUtility.QueryContext<Position> queryContext = QueryUtility.prepareContext(Position.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
                    .addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), Position_.POSITION_ID, queryContext));
            Order order = null;
            if (query.getSort().isById())
                order = QueryUtility.getDomainIdOrder(Position_.POSITION_ID, queryContext, query.getSort().isAsc());
            queryContext.setOrder(order);
            return QueryUtility.pagedQuery(query, queryContext);
        }
    }
}
