package com.mt.proxy.domain.rate_limit;

import com.mt.proxy.domain.DomainRegistry;
import com.mt.proxy.domain.Endpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    private final Map<Endpoint, EndpointSubInfo> configs = new HashMap<>();

    public void updateConfig(Set<Endpoint> endpoints, Set<Subscription> subscriptions) {
        endpoints.forEach(ep -> configs.put(ep, new EndpointSubInfo(ep, subscriptions)));
    }

    public void refreshCache() {
        Set<Subscription> subscriptions =
            DomainRegistry.getRetrieveSubscriptionService().loadAllSubscriptions();
        Set<Endpoint> cachedEndpoints = DomainRegistry.getEndpointService().getCachedEndpoints();
        updateConfig(cachedEndpoints, subscriptions);
    }

    /**
     * get subscription, for public endpoint use endpoint self config
     * @param endpoint endpoint
     * @param projectId projectId
     * @return subscription
     */
    public Subscription getSubscriptionInfo(Endpoint endpoint, @Nullable String projectId) {
        if (!endpoint.isSecured()) {
            return new Subscription(endpoint);
        }
        EndpointSubInfo endpointSubInfo = configs.get(endpoint);
        if (endpointSubInfo == null) {
            return null;
        }
        return endpointSubInfo.getSubForProject(projectId);
    }
}
