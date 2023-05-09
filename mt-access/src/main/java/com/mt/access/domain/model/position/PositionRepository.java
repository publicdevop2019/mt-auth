package com.mt.access.domain.model.position;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;

public interface PositionRepository {
    void add(Position role);

    SumPagedRep<Position> query(PositionQuery roleQuery);

    void remove(Position e);

    default Position get(PositionId id){
        Position position = query(id);
        Validator.notNull(position);
        return position;
    }
    Position query(PositionId id);
}
