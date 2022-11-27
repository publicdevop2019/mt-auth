package com.mt.access.domain.model.sub_request;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.position.PositionQuery;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.Set;

public interface SubRequestRepository {
    void add(SubRequest subRequest);

    SumPagedRep<SubRequest> getByQuery(SubRequestQuery query);

    SumPagedRep<SubRequest> getMySubscriptions(SubRequestQuery query);

    void remove(SubRequest e);

    Optional<SubRequest> getById(SubRequestId id);

    Set<UserId> getEndpointSubscriber(EndpointId endpointId);

    SumPagedRep<SubRequest> getSubscription(SubRequestQuery query);

    Set<EndpointId> getSubscribeEndpointIds(UserId userId);
}
