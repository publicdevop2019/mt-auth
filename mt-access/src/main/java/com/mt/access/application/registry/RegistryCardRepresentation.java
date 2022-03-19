package com.mt.access.application.registry;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class RegistryCardRepresentation {
    private final Integer count;
    private String name;
    private String id;

    public RegistryCardRepresentation(Application application) {
        this.name = application.getName();
        this.id = application.getName();
        List<InstanceInfo> instances = application.getInstances();
        count = instances.size();
    }

    public static void updateDetails(List<RegistryCardRepresentation> info) {
        Set<ClientId> collect = info.stream().map(e -> new ClientId(e.name)).collect(Collectors.toSet());
        Set<Client> clients = ApplicationServiceRegistry.getClientApplicationService().findAllByIds(collect);
        info.forEach(e -> clients.stream().filter(ee -> ee.getClientId().equals(new ClientId(e.name))).findFirst().ifPresent(ee -> {
            e.name = ee.getName();
        }));
    }
}
