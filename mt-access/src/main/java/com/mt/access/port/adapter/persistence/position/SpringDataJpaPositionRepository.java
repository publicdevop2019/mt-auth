package com.mt.access.port.adapter.persistence.position;

import com.mt.access.domain.model.position.Position;
import com.mt.access.domain.model.position.PositionId;
import com.mt.access.domain.model.position.PositionQuery;
import com.mt.access.domain.model.position.PositionRepository;
import com.mt.access.domain.model.position.Position_;
import com.mt.access.port.adapter.persistence.QueryBuilderRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

public interface SpringDataJpaPositionRepository
    extends PositionRepository, JpaRepository<Position, Long> {
    default Position query(PositionId id) {
        return query(new PositionQuery(id)).findFirst().orElse(null);
    }

    default void add(Position position) {
        save(position);
    }

    default void remove(Position position) {
        delete(position);
    }

    default SumPagedRep<Position> query(PositionQuery positionquery) {
        return QueryBuilderRegistry.getPositionAdaptor().execute(positionquery);
    }

    @Component
    class JpaCriteriaApiPositionAdaptor {
        public SumPagedRep<Position> execute(PositionQuery query) {
            QueryUtility.QueryContext<Position> queryContext =
                QueryUtility.prepareContext(Position.class, query);
            Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility
                .addDomainIdInPredicate(
                    e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()),
                    Position_.POSITION_ID, queryContext));
            Order order = null;
            if (Checker.isTrue(query.getSort().getById())) {
                order = QueryUtility
                    .getDomainIdOrder(Position_.POSITION_ID, queryContext, query.getSort().getIsAsc());
            }
            queryContext.setOrder(order);
            return QueryUtility.nativePagedQuery(query, queryContext);
        }
    }
}
