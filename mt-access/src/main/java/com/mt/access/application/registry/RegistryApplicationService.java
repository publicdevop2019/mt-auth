package com.mt.access.application.registry;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import java.util.List;
import java.util.Set;
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
        List<RegistryCardRepresentation> collect =
            registeredApplications.stream().map(RegistryCardRepresentation::new)
                .collect(Collectors.toList());
        Set<ClientId> collect2 =
            collect.stream().map(e -> new ClientId(e.getName()))
                .collect(Collectors.toSet());
        Set<Client> clients = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getClientRepository().query(e),
                new ClientQuery(collect2));
        collect.forEach(
            e -> clients.stream()
                .filter(ee -> ee.getClientId().equals(new ClientId(e.getName())))
                .findFirst().ifPresent(ee -> {
                    e.setName(ee.getName());
                }));
        return collect;
    }
}
