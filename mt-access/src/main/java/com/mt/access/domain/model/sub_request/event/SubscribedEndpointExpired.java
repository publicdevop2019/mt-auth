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
public class SubscribedEndpointExpired extends DomainEvent {
    public static final String SUBSCRIBED_ENDPOINT_EXPIRE = "subscribed_endpoint_expire";
    public static final String name = "SUBSCRIBED_ENDPOINT_EXPIRE";
    private EndpointId endpointId;

    {
        setTopic(SUBSCRIBED_ENDPOINT_EXPIRE);
        setName(name);

    }

    public SubscribedEndpointExpired(EndpointId endpointId,
                                     Set<UserId> ids) {
        super(new HashSet<>(ids));
        this.endpointId = endpointId;
    }
}
