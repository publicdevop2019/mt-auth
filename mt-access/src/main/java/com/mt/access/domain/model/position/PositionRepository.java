package com.mt.access.domain.model.position;

import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;

public interface PositionRepository {
    void add(Position role);

    SumPagedRep<Position> getByQuery(PositionQuery roleQuery);

    void remove(Position e);

    Optional<Position> getById(PositionId id);
}
