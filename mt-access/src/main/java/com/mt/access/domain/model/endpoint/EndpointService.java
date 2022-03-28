package com.mt.access.domain.model.endpoint;

import com.mt.access.domain.model.endpoint.event.EndpointReloadRequested;
import com.mt.common.domain.CommonDomainRegistry;
import org.springframework.stereotype.Service;

@Service
public class EndpointService {
    public void reloadEndpointCache() {
        CommonDomainRegistry.getDomainEventRepository().append(new EndpointReloadRequested());
    }
}
