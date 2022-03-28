package com.mt.access.application.registry;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistryApplicationService {
    @Autowired
    private EurekaClient discoveryClient;

    public List<RegistryCardRepresentation> getInfo() {
        List<Application> registeredApplications =
            discoveryClient.getApplications().getRegisteredApplications();
        return registeredApplications.stream().map(RegistryCardRepresentation::new)
            .collect(Collectors.toList());
    }
}
