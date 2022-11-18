package com.mt.access.domain.model.sub_request;

import com.mt.access.domain.model.position.PositionQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;

public interface SubRequestRepository {
    void add(SubRequest subRequest);

    SumPagedRep<SubRequest> getByQuery(SubRequestQuery query);

    SumPagedRep<SubRequest> getMySubscriptions(SubRequestQuery query);

    void remove(SubRequest e);

    Optional<SubRequest> getById(SubRequestId id);
}
