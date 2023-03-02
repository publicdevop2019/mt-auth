package com.mt.access.domain.model.sub_request.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AuditEvent
public class SubscriberEndpointExpireEvent extends DomainEvent {
    public static final String SUBSCRIBER_ENDPOINT_EXPIRE = "subscriber_endpoint_expire";
    public static final String name = "SUBSCRIBER_ENDPOINT_EXPIRE";
    private EndpointId endpointId;

    public SubscriberEndpointExpireEvent(EndpointId endpointId,
                                         Set<UserId> ids) {
        super();
        setTopic(SUBSCRIBER_ENDPOINT_EXPIRE);
        setName(name);
        setDomainIds(new HashSet<>(ids));
        this.endpointId = endpointId;
    }
}
