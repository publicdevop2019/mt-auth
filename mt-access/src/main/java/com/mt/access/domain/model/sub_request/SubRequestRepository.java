package com.mt.access.domain.model.sub_request;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Set;

public interface SubRequestRepository {
    void add(SubRequest subRequest);

    SumPagedRep<SubRequest> query(SubRequestQuery query);

    SumPagedRep<SubRequest> getMySubscriptions(SubRequestQuery query);

    void remove(SubRequest e);

    default SubRequest get(SubRequestId id) {
        SubRequest subRequest = query(id);
        Validator.notNull(subRequest);
        return subRequest;
    }


    SubRequest query(SubRequestId id);

    Set<UserId> getEndpointSubscriber(EndpointId endpointId);

    SumPagedRep<SubRequest> getSubscription(SubRequestQuery query);

    Set<EndpointId> getSubscribeEndpointIds(ProjectId projectId);

    void update(SubRequest old, SubRequest update);
}
