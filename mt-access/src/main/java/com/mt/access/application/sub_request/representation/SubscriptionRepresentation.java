package com.mt.access.application.sub_request.representation;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.sub_request.SubRequest;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubscriptionRepresentation extends BasicSubRequest{
    private final String id;
    private final int maxInvokePerSecond;
    private final int maxInvokePerMinute;
    private String endpointStatus;

    public SubscriptionRepresentation(SubRequest subRequest) {
        super(subRequest);
        id = subRequest.getSubRequestId().getDomainId();
        maxInvokePerSecond = subRequest.getMaxInvokePerSec();
        maxInvokePerMinute = subRequest.getMaxInvokePerMin();
    }

    /**
     * update view with endpoint name and status
     * @param list paginated data
     */
    public static void updateEndpointDetails(SumPagedRep<SubscriptionRepresentation> list) {
        if (!list.getData().isEmpty()) {
            Set<EndpointId> collect =
                list.getData().stream().map(e -> new EndpointId(e.getEndpointId()))
                    .collect(Collectors.toSet());
            Set<Endpoint> collect2 =
                ApplicationServiceRegistry.getEndpointApplicationService().internalQuery(collect);
            list.getData().forEach(e -> collect2.stream().filter(ee ->
                ee.getEndpointId().equals(new EndpointId(e.getEndpointId()))).findAny()
                .ifPresent(eee -> {
                    e.setEndpointName(eee.getName());
                    e.setEndpointStatus("TODO");
                }));
        }
    }
}
