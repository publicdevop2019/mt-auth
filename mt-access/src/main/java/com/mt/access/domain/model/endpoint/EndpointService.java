package com.mt.access.domain.model.endpoint;

import com.mt.access.domain.model.endpoint.event.EndpointReloadRequested;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import org.springframework.stereotype.Service;

@Service
public class EndpointService {
    public void reloadEndpointCache(TransactionContext context) {
        context.append(new EndpointReloadRequested());
    }
}
