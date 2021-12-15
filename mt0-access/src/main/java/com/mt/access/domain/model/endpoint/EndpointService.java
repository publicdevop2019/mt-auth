package com.mt.access.domain.model.endpoint;

import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.access.domain.model.endpoint.event.EndpointReloadRequested;
import org.springframework.stereotype.Service;

@Service
public class EndpointService {
    public void reloadEndpointCache() {
        DomainEventPublisher.instance().publish(new EndpointReloadRequested());
    }
}
