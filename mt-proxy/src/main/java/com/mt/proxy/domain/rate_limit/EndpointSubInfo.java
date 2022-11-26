package com.mt.proxy.domain.rate_limit;

import com.mt.proxy.domain.Endpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EndpointSubInfo {
    private final Map<String, Subscription> projectSubs = new HashMap<>();

    public EndpointSubInfo(Endpoint ep, Set<Subscription> subscriptions) {
        projectSubs.put(ep.getProjectId(), new Subscription(ep));
        subscriptions.stream().filter(e -> e.getEndpointId().equals(ep.getId())).forEach(sub -> {
            projectSubs.put(sub.getProjectId(), sub);
        });
    }

    public Subscription getSubForProject(String projectId) {
        return projectSubs.get(projectId);
    }
}
